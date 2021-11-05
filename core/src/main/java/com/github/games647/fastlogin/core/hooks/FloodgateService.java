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
package com.github.games647.fastlogin.core.hooks;

import com.github.games647.craftapi.model.Profile;
import com.github.games647.craftapi.resolver.RateLimitException;
import com.github.games647.fastlogin.core.StoredProfile;
import com.github.games647.fastlogin.core.shared.FastLoginCore;
import com.github.games647.fastlogin.core.shared.LoginSource;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class FloodgateService {
    
    private final FloodgateApi floodgate;
    private final FastLoginCore < ?, ?, ? > core;
    
    public FloodgateService( FloodgateApi floodgate , FastLoginCore < ?, ?, ? > core ){
        this.floodgate = floodgate;
        this.core = core;
    }
    
    /**
     * Checks if a config entry (related to Floodgate) is valid. <br>
     * Writes to Log if the value is invalid.
     * <p>
     * This should be used for:
     * <ul>
     * <li>allowFloodgateNameConflict
     * <li>autoLoginFloodgate
     * <li>autoRegisterFloodgate
     * </ul>
     * </p>
     *
     * @param key the key of the entry in config.yml
     *
     * @return <b>true</b> if the entry's value is "true", "false", or "linked"
     */
    public boolean isValidFloodgateConfigString( String key ){
        String value = core.getConfig( ).get( key ).toString( ).toLowerCase( Locale.ENGLISH );
        if ( !value.equals( "true" ) && !value.equals( "linked" ) && !value.equals( "false" ) && !value.equals( "no-conflict" ) ) {
            core.getPlugin( ).getLog( ).error( "Invalid value detected for {} in FastLogin/config.yml." , key );
            return false;
        }
        
        return true;
    }
    
    public boolean isUsernameForbidden( StoredProfile profile ){
        String playerPrefix = FloodgateApi.getInstance( ).getPlayerPrefix( );
        return profile.getName( ).startsWith( playerPrefix ) && !playerPrefix.isEmpty( );
    }
    
    /**
     * Check if the player's name conflicts an existing Java player's name, and
     * kick them if it does
     *
     * @param username the name of the player
     * @param source   an instance of LoginSource
     */
    public void checkNameConflict( String username , LoginSource source ){
        String allowConflict = core.getConfig( ).get( "allowFloodgateNameConflict" ).toString( ).toLowerCase( );
        
        // check if the Bedrock player is linked to a Java account
        FloodgatePlayer floodgatePlayer = getFloodgatePlayer( username );
        boolean isLinked = floodgatePlayer.getLinkedPlayer( ) != null;
        
        if ( "false".equals( allowConflict )
                || "linked".equals( allowConflict ) && !isLinked ) {
            
            // check for conflicting Premium Java name
            Optional < Profile > premiumUUID = Optional.empty( );
            try {
                premiumUUID = core.getResolver( ).findProfile( username );
            } catch ( IOException | RateLimitException e ) {
                core.getPlugin( ).getLog( ).error(
                        "Could not check whether Floodgate Player {}'s name conflicts a premium Java player's name." ,
                        username );
                try {
                    source.kick( "Could not check if your name conflicts an existing premium Java account's name.\n"
                            + "This is usually a serverside error." );
                } catch ( Exception ex ) {
                    core.getPlugin( ).getLog( ).error( "Could not kick Player {}" , username , ex );
                }
            }
            
            if ( premiumUUID.isPresent( ) ) {
                core.getPlugin( ).getLog( ).info( "Bedrock Player {}'s name conflicts an existing premium Java account's name" ,
                        username );
                try {
                    source.kick( "Your name conflicts an existing premium Java account's name" );
                } catch ( Exception ex ) {
                    core.getPlugin( ).getLog( ).error( "Could not kick Player {}" , username , ex );
                }
            }
        } else {
            core.getPlugin( ).getLog( ).info( "Skipping name conflict checking for player {}" , username );
        }
    }
    
    /**
     * The FloodgateApi does not support querying players by name, so this function
     * iterates over every online FloodgatePlayer and checks if the requested
     * username can be found
     * <br>
     * <i>Falls back to non-prefixed name checks, if ProtocolLib is installed</i>
     *
     * @param prefixedUsername the name of the player with the prefix appended
     *
     * @return FloodgatePlayer if found, null otherwise
     */
    public FloodgatePlayer getFloodgatePlayer( String prefixedUsername ){
        //prefixes are broken with ProtocolLib, so fall back to name checks without prefixes
        //this should be removed if #493 gets fixed
        if ( core.getPlugin( ).isPluginInstalled( "ProtocolLib" ) ) {
            for ( FloodgatePlayer floodgatePlayer : FloodgateApi.getInstance( ).getPlayers( ) ) {
                if ( floodgatePlayer.getUsername( ).equals( prefixedUsername ) ) {
                    return floodgatePlayer;
                }
            }
            return null;
        }
        for ( FloodgatePlayer floodgatePlayer : FloodgateApi.getInstance( ).getPlayers( ) ) {
            if ( floodgatePlayer.getCorrectUsername( ).equals( prefixedUsername ) ) {
                return floodgatePlayer;
            }
        }
        
        return null;
    }
    
    public FloodgatePlayer getFloodgatePlayer( UUID uuid ){
        return FloodgateApi.getInstance( ).getPlayer( uuid );
    }
    
    public boolean isFloodgatePlayer( UUID uuid ){
        return getFloodgatePlayer( uuid ) != null;
    }
    
    public boolean isFloodgateConnection( String username ){
        return getFloodgatePlayer( username ) != null;
    }
}
