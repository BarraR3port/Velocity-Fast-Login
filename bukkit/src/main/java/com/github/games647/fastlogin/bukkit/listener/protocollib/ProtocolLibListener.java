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
package com.github.games647.fastlogin.bukkit.listener.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.games647.fastlogin.bukkit.FastLoginBukkit;
import com.github.games647.fastlogin.core.RateLimiter;
import org.bukkit.entity.Player;

import java.security.KeyPair;
import java.security.SecureRandom;

import static com.comphenix.protocol.PacketType.Login.Client.ENCRYPTION_BEGIN;
import static com.comphenix.protocol.PacketType.Login.Client.START;

public class ProtocolLibListener extends PacketAdapter {
    
    public static final String SOURCE_META_KEY = "source";
    
    private final FastLoginBukkit plugin;
    
    //just create a new once on plugin enable. This used for verify token generation
    private final SecureRandom random = new SecureRandom( );
    private final KeyPair keyPair = EncryptionUtil.generateKeyPair( );
    private final RateLimiter rateLimiter;
    
    public ProtocolLibListener( FastLoginBukkit plugin , RateLimiter rateLimiter ){
        //run async in order to not block the server, because we are making api calls to Mojang
        super( params( )
                .plugin( plugin )
                .types( START , ENCRYPTION_BEGIN )
                .optionAsync( ) );
        
        this.plugin = plugin;
        this.rateLimiter = rateLimiter;
    }
    
    public static void register( FastLoginBukkit plugin , RateLimiter rateLimiter ){
        // they will be created with a static builder, because otherwise it will throw a NoClassDefFoundError
        // TODO: make synchronous processing, but do web or database requests async
        ProtocolLibrary.getProtocolManager( )
                .getAsynchronousManager( )
                .registerAsyncHandler( new ProtocolLibListener( plugin , rateLimiter ) )
                .start( );
    }
    
    @Override
    public void onPacketReceiving( PacketEvent packetEvent ){
        if ( packetEvent.isCancelled( )
                || plugin.getCore( ).getAuthPluginHook( ) == null
                || !plugin.isServerFullyStarted( ) ) {
            return;
        }
        
        if ( isFastLoginPacket( packetEvent ) ) {
            // this is our own packet
            return;
        }
        
        Player sender = packetEvent.getPlayer( );
        PacketType packetType = packetEvent.getPacketType( );
        if ( packetType == START ) {
            if ( !rateLimiter.tryAcquire( ) ) {
                plugin.getLog( ).warn( "Simple Anti-Bot join limit - Ignoring {}" , sender );
                return;
            }
            
            onLogin( packetEvent , sender );
        } else {
            onEncryptionBegin( packetEvent , sender );
        }
    }
    
    private Boolean isFastLoginPacket( PacketEvent packetEvent ){
        return packetEvent.getPacket( ).getMeta( SOURCE_META_KEY )
                .map( val -> val.equals( plugin.getName( ) ) )
                .orElse( false );
    }
    
    private void onEncryptionBegin( PacketEvent packetEvent , Player sender ){
        byte[] sharedSecret = packetEvent.getPacket( ).getByteArrays( ).read( 0 );
        
        packetEvent.getAsyncMarker( ).incrementProcessingDelay( );
        Runnable verifyTask = new VerifyResponseTask( plugin , packetEvent , sender , sharedSecret , keyPair );
        plugin.getScheduler( ).runAsync( verifyTask );
    }
    
    private void onLogin( PacketEvent packetEvent , Player player ){
        //this includes ip:port. Should be unique for an incoming login request with a timeout of 2 minutes
        String sessionKey = player.getAddress( ).toString( );
        
        //remove old data every time on a new login in order to keep the session only for one person
        plugin.removeSession( player.getAddress( ) );
        
        //player.getName() won't work at this state
        PacketContainer packet = packetEvent.getPacket( );
        
        String username = packet.getGameProfiles( ).read( 0 ).getName( );
        plugin.getLog( ).trace( "GameProfile {} with {} connecting" , sessionKey , username );
        
        packetEvent.getAsyncMarker( ).incrementProcessingDelay( );
        Runnable nameCheckTask = new NameCheckTask( plugin , random , player , packetEvent , username , keyPair.getPublic( ) );
        plugin.getScheduler( ).runAsync( nameCheckTask );
    }
}
