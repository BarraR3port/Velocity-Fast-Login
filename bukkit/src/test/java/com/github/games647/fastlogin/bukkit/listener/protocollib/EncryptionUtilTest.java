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

import org.junit.Test;

import java.security.SecureRandom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class EncryptionUtilTest {
    
    @Test
    public void testVerifyToken( ) throws Exception{
        SecureRandom random = new SecureRandom( );
        byte[] token = EncryptionUtil.generateVerifyToken( random );
        
        assertThat( token , notNullValue( ) );
        assertThat( token.length , is( 4 ) );
    }
    
    // @Test
    // public void testDecryptSharedSecret() throws Exception {
    //
    // }
    //
    // @Test
    // public void testDecryptData() throws Exception {
    //
    // }
    
    // private static SecretKey createNewSharedKey() {
    //     try {
    //         KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
    //         keygenerator.init(128);
    //         return keygenerator.generateKey();
    //     } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
    //         throw new Error(nosuchalgorithmexception);
    //     }
    // }
}
