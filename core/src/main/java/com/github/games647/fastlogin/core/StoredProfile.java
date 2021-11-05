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
package com.github.games647.fastlogin.core;

import com.github.games647.craftapi.model.Profile;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class StoredProfile extends Profile {
    
    private final ReentrantLock saveLock = new ReentrantLock( );
    private long rowId;
    private boolean premium;
    private String lastIp;
    private Instant lastLogin;
    
    public StoredProfile( long rowId , UUID uuid , String playerName , boolean premium , String lastIp , Instant lastLogin ){
        super( uuid , playerName );
        
        this.rowId = rowId;
        this.premium = premium;
        this.lastIp = lastIp;
        this.lastLogin = lastLogin;
    }
    
    public StoredProfile( UUID uuid , String playerName , boolean premium , String lastIp ){
        this( -1 , uuid , playerName , premium , lastIp , Instant.now( ) );
    }
    
    public ReentrantLock getSaveLock( ){
        return saveLock;
    }
    
    public synchronized boolean isSaved( ){
        return rowId >= 0;
    }
    
    public synchronized void setPlayerName( String playerName ){
        this.name = playerName;
    }
    
    public synchronized long getRowId( ){
        return rowId;
    }
    
    public synchronized void setRowId( long generatedId ){
        this.rowId = generatedId;
    }
    
    // can be null
    public synchronized UUID getId( ){
        return id;
    }
    
    public synchronized void setId( UUID uniqueId ){
        this.id = uniqueId;
    }
    
    public synchronized Optional < UUID > getOptId( ){
        return Optional.ofNullable( id );
    }
    
    public synchronized boolean isPremium( ){
        return premium;
    }
    
    public synchronized void setPremium( boolean premium ){
        this.premium = premium;
    }
    
    public synchronized String getLastIp( ){
        return lastIp;
    }
    
    public synchronized void setLastIp( String lastIp ){
        this.lastIp = lastIp;
    }
    
    public synchronized Instant getLastLogin( ){
        return lastLogin;
    }
    
    public synchronized void setLastLogin( Instant lastLogin ){
        this.lastLogin = lastLogin;
    }
    
    @Override
    public boolean equals( Object o ){
        if ( this == o ) return true;
        if ( !(o instanceof StoredProfile) ) return false;
        if ( !super.equals( o ) ) return false;
        StoredProfile that = ( StoredProfile ) o;
        return rowId == that.rowId && premium == that.premium
                && Objects.equals( lastIp , that.lastIp ) && lastLogin.equals( that.lastLogin );
    }
    
    @Override
    public int hashCode( ){
        return Objects.hash( super.hashCode( ) , rowId , premium , lastIp , lastLogin );
    }
    
    @Override
    public synchronized String toString( ){
        return this.getClass( ).getSimpleName( ) + '{' +
                "rowId=" + rowId +
                ", premium=" + premium +
                ", lastIp='" + lastIp + '\'' +
                ", lastLogin=" + lastLogin +
                "} " + super.toString( );
    }
}
