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
package com.github.games647.fastlogin.velocity.task;

import com.github.games647.fastlogin.core.StoredProfile;
import com.github.games647.fastlogin.core.shared.FastLoginCore;
import com.github.games647.fastlogin.core.shared.event.FastLoginPremiumToggleEvent.PremiumToggleReason;
import com.github.games647.fastlogin.velocity.FastLoginVelocity;
import com.github.games647.fastlogin.velocity.event.VelocityFastLoginPremiumToggleEvent;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AsyncToggleMessage implements Runnable {
    
    private final FastLoginCore < Player, CommandSource, FastLoginVelocity > core;
    private final CommandSource sender;
    private final String senderName;
    private final String targetPlayer;
    private final boolean toPremium;
    private final boolean isPlayerSender;
    
    public AsyncToggleMessage( FastLoginCore < Player, CommandSource, FastLoginVelocity > core ,
                               CommandSource sender , String playerName , boolean toPremium , boolean playerSender ){
        this.core = core;
        this.sender = sender;
        this.targetPlayer = playerName;
        this.toPremium = toPremium;
        this.isPlayerSender = playerSender;
        if ( sender instanceof Player )
            senderName = (( Player ) sender).getUsername( );
        else
            senderName = "";
    }
    
    @Override
    public void run( ){
        if ( toPremium ) {
            activatePremium( );
        } else {
            turnOffPremium( );
        }
    }
    
    private void turnOffPremium( ){
        StoredProfile playerProfile = core.getStorage( ).loadProfile( targetPlayer );
        //existing player is already cracked
        if ( playerProfile.isSaved( ) && !playerProfile.isPremium( ) ) {
            sendMessage( "not-premium" );
            return;
        }
        
        playerProfile.setPremium( false );
        playerProfile.setId( null );
        core.getStorage( ).save( playerProfile );
        PremiumToggleReason reason = (!isPlayerSender || !senderName.equalsIgnoreCase( playerProfile.getName( ) )) ?
                PremiumToggleReason.COMMAND_OTHER : PremiumToggleReason.COMMAND_SELF;
        core.getPlugin( ).getProxy( ).getEventManager( ).fire(
                new VelocityFastLoginPremiumToggleEvent( playerProfile , reason ) );
        sendMessage( "remove-premium" );
    }
    
    private void activatePremium( ){
        StoredProfile playerProfile = core.getStorage( ).loadProfile( targetPlayer );
        if ( playerProfile.isPremium( ) ) {
            sendMessage( "already-exists" );
            return;
        }
        
        playerProfile.setPremium( true );
        core.getStorage( ).save( playerProfile );
        PremiumToggleReason reason = (!isPlayerSender || !senderName.equalsIgnoreCase( playerProfile.getName( ) )) ?
                PremiumToggleReason.COMMAND_OTHER : PremiumToggleReason.COMMAND_SELF;
        core.getPlugin( ).getProxy( ).getEventManager( ).fire(
                new VelocityFastLoginPremiumToggleEvent( playerProfile , reason ) );
        sendMessage( "add-premium" );
    }
    
    private void sendMessage( String localeId ){
        String message = core.getMessage( localeId );
        if ( isPlayerSender ) {
            sender.sendMessage( LegacyComponentSerializer.legacyAmpersand( ).deserialize( message ) );
        } else {
            core.getPlugin( ).getProxy( ).getConsoleCommandSource( ).sendMessage( LegacyComponentSerializer.legacyAmpersand( ).deserialize( message ) );
        }
    }
}
