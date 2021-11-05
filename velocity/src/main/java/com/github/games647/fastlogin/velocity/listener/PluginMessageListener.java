/*
 * SPDX-License-Identifier: MIT
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 <Your name and contributors>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.games647.fastlogin.velocity.listener;

import com.github.games647.fastlogin.core.StoredProfile;
import com.github.games647.fastlogin.core.message.ChangePremiumMessage;
import com.github.games647.fastlogin.core.message.SuccessMessage;
import com.github.games647.fastlogin.core.shared.FastLoginCore;
import com.github.games647.fastlogin.velocity.FastLoginVelocity;
import com.github.games647.fastlogin.velocity.VelocityLoginSession;
import com.github.games647.fastlogin.velocity.task.AsyncToggleMessage;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;

public class PluginMessageListener {
    
    private final FastLoginVelocity plugin;
    
    private final String successChannel;
    private final String changeChannel;
    
    public PluginMessageListener( FastLoginVelocity plugin ){
        this.plugin = plugin;
        
        this.successChannel = MinecraftChannelIdentifier.create( plugin.getName( ) , SuccessMessage.SUCCESS_CHANNEL ).getId( );
        this.changeChannel = MinecraftChannelIdentifier.create( plugin.getName( ) , ChangePremiumMessage.CHANGE_CHANNEL ).getId( );
    }
    
    @Subscribe
    public void onPluginMessage( PluginMessageEvent pluginMessageEvent ){
        String channel = pluginMessageEvent.getIdentifier( ).getId( );
        if ( !pluginMessageEvent.getResult( ).isAllowed( ) || !channel.startsWith( plugin.getName( ).toLowerCase( ) ) ) {
            return;
        }
        
        //the client shouldn't be able to read the messages in order to know something about server internal states
        //moreover the client shouldn't be able fake a running premium check by sending the result message
        pluginMessageEvent.setResult( PluginMessageEvent.ForwardResult.handled( ) );
        
        if ( !(pluginMessageEvent.getSource( ) instanceof ServerConnection) ) {
            //check if the message is sent from the server
            return;
        }
        
        //so that we can safely process this in the background
        byte[] data = Arrays.copyOf( pluginMessageEvent.getData( ) , pluginMessageEvent.getData( ).length );
        Player forPlayer = ( Player ) pluginMessageEvent.getTarget( );
        
        plugin.getScheduler( ).runAsync( ( ) -> readMessage( forPlayer , channel , data ) );
    }
    
    private void readMessage( Player forPlayer , String channel , byte[] data ){
        FastLoginCore < Player, CommandSource, FastLoginVelocity > core = plugin.getCore( );
        
        ByteArrayDataInput dataInput = ByteStreams.newDataInput( data );
        if ( successChannel.equals( channel ) ) {
            onSuccessMessage( forPlayer );
        } else if ( changeChannel.equals( channel ) ) {
            ChangePremiumMessage changeMessage = new ChangePremiumMessage( );
            changeMessage.readFrom( dataInput );
            
            String playerName = changeMessage.getPlayerName( );
            boolean isSourceInvoker = changeMessage.isSourceInvoker( );
            if ( changeMessage.shouldEnable( ) ) {
                if ( playerName.equals( forPlayer.getUsername( ) ) && plugin.getCore( ).getConfig( ).get( "premium-warning" , true )
                        && !core.getPendingConfirms( ).contains( forPlayer.getUniqueId( ) ) ) {
                    String message = core.getMessage( "premium-warning" );
                    forPlayer.sendMessage( LegacyComponentSerializer.legacyAmpersand( ).deserialize( message ) );
                    core.getPendingConfirms( ).add( forPlayer.getUniqueId( ) );
                    return;
                }
                
                core.getPendingConfirms( ).remove( forPlayer.getUniqueId( ) );
                Runnable task = new AsyncToggleMessage( core , forPlayer , playerName , true , isSourceInvoker );
                plugin.getScheduler( ).runAsync( task );
            } else {
                Runnable task = new AsyncToggleMessage( core , forPlayer , playerName , false , isSourceInvoker );
                plugin.getScheduler( ).runAsync( task );
            }
        }
    }
    
    private void onSuccessMessage( Player forPlayer ){
        if ( forPlayer.isOnlineMode( ) ) {
            //bukkit module successfully received and force logged in the user
            //update only on success to prevent corrupt data
            VelocityLoginSession loginSession = plugin.getSession( ).get( forPlayer.getRemoteAddress( ) );
            StoredProfile playerProfile = loginSession.getProfile( );
            loginSession.setRegistered( true );
            if ( !loginSession.isAlreadySaved( ) ) {
                playerProfile.setPremium( true );
                plugin.getCore( ).getStorage( ).save( playerProfile );
                loginSession.setAlreadySaved( true );
            }
        }
    }
}
