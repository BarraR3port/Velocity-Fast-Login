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
package com.github.games647.fastlogin.bukkit;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PremiumPlaceholder extends PlaceholderExpansion {
    
    private static final String PLACEHOLDER_VARIABLE = "status";
    
    private final FastLoginBukkit plugin;
    
    public PremiumPlaceholder( FastLoginBukkit plugin ){
        this.plugin = plugin;
    }
    
    @Override
    public String onRequest( OfflinePlayer player , String identifier ){
        // player is null if offline
        if ( player != null && PLACEHOLDER_VARIABLE.equals( identifier ) ) {
            return plugin.getStatus( player.getUniqueId( ) ).getReadableName( );
        }
        
        return null;
    }
    
    @Override
    public String getIdentifier( ){
        return plugin.getName( );
    }
    
    @Override
    public String getRequiredPlugin( ){
        return plugin.getName( );
    }
    
    @Override
    public String getAuthor( ){
        return String.join( ", " , plugin.getDescription( ).getAuthors( ) );
    }
    
    @Override
    public String getVersion( ){
        return plugin.getDescription( ).getVersion( );
    }
}
