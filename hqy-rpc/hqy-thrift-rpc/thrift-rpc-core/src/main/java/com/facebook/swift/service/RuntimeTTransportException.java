/*
 * Copyright (C) 2012 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.facebook.swift.service;

import org.apache.thrift.transport.TTransportException;

/**
 * Runtime equivalent of TTransportException.  If a swift client receives a
 * TTransportException for a method that doesn't declare TTransportException
 * to be thrown, the underlying exception is wrapped in this class and
 * rethrown.
 */
public class RuntimeTTransportException extends RuntimeTException {
    private static final long serialVersionUID = 1L;

    public RuntimeTTransportException(String message, TTransportException cause) {
        super(message, cause);
    }
}
