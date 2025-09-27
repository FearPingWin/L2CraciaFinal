/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: Redistributions of source code
 * must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the Sun Microsystems nor the names of
 * is contributors may be used to endorse or promote products derived from this software
 * without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * MemoryJavaFileManager.java
 * @author A. Sundararajan
 */

package com.sun.script.java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JavaFileManager that keeps compiled .class bytes in memory.
 */
public final class MemoryJavaFileManager extends EclipseFileManager
{
	/** Java source file extension. */
	private final static String EXT = ".java";
	private Map<String, byte[]> classBytes;
	private static final Log _log = LogFactory.getLog(MemoryJavaFileManager.class);
	
	public MemoryJavaFileManager()
	{
		super(null, null);
		classBytes = new HashMap<String, byte[]>();
	}
	
	public Map<String, byte[]> getClassBytes()
	{
		return classBytes;
	}
	
	@Override
	public void close() throws IOException
	{
		classBytes = new HashMap<String, byte[]>();
	}
	
	@Override
	public void flush() throws IOException
	{
	}
	
	/**
	 * A file object used to represent Java source coming from a string.
	 */
	private static class StringInputBuffer extends SimpleJavaFileObject
	{
		final String code;
        
		StringInputBuffer(String name, String code)
		{
			super(toURI(name), Kind.SOURCE);
			this.code = code;
		}

		StringInputBuffer(String name, String code, String sourcePath)
		{
			super(toURI(name, sourcePath), Kind.SOURCE);
			this.code = code;
		}
        
		@Override
		public CharBuffer getCharContent(boolean ignoreEncodingErrors)
		{
			return CharBuffer.wrap(code);
		}
        
		public Reader openReader()
		{
			return new StringReader(code);
		}
	}
	
	/**
	 * A file object that stores Java bytecode into the classBytes map.
	 */
	private class ClassOutputBuffer extends SimpleJavaFileObject
	{
		private String name;
		
		ClassOutputBuffer(String name)
		{
			super(toURI(name), Kind.CLASS);
			this.name = name;
		}
		
		@Override
		public OutputStream openOutputStream()
		{
			return new FilterOutputStream(new ByteArrayOutputStream()) {
				@Override
				public void close() throws IOException
				{
					out.close();
					ByteArrayOutputStream bos = (ByteArrayOutputStream)out;
					classBytes.put(name, bos.toByteArray());
				}
			};
		}
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, Kind kind,
			FileObject sibling) throws IOException
	{
		if (kind == Kind.CLASS)
		{
			return new ClassOutputBuffer(className.replace('/', '.'));
		}
		else
		{
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

	/**
	 * Helper to create a JavaFileObject from a string source and optional sourcePath.
	 */
	static JavaFileObject makeStringSource(String name, String code)
	{
		return new StringInputBuffer(name, code);
	}

	static JavaFileObject makeStringSource(String name, String code, String sourcePath)
	{
		return new StringInputBuffer(name, code, sourcePath);
	}

	static URI toURI(String name)
	{
		return toURI(name, null);
	}

	static URI toURI(String name, String sourcePath)
	{
		// Try the name as provided first
		try
		{
			if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: name='" + name + "' sourcePath='" + sourcePath + "'");
			File file = new File(name);
			if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: File(name).exists=" + file.exists() + " -> " + file.getAbsolutePath());
			if (file.exists())
			{
				if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: using File(name)");
				return file.toURI();
			}

			// Normalize leading slash on Windows (e.g. "/MC_Show.java")
			String normalized = name;
			if (normalized.length() > 0 && (normalized.charAt(0) == '/' || normalized.charAt(0) == '\\'))
			{
				normalized = normalized.substring(1);
			}

			// Convert forward slashes to platform separators
			normalized = normalized.replace('/', File.separatorChar).replace('\\', File.separatorChar);

			// Try normalized relative path
			File nf = new File(normalized);
			if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: normalized='" + normalized + "' -> exists=" + nf.exists() + " -> " + nf.getAbsolutePath());
			if (nf.exists())
			{
				if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: using normalized path");
				return nf.toURI();
			}

			// If a sourcePath is provided, try searching there
			if (sourcePath != null)
			{
				File sf = new File(sourcePath, normalized);
				if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: source trial 1='" + sf.getAbsolutePath() + "' exists=" + sf.exists());
				if (sf.exists())
				{
					if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: using sourcePath/normalized");
					return sf.toURI();
				}

				// Also try with just the base name in the sourcePath
				File base = new File(normalized).getName().length() > 0 ? new File(sourcePath, new File(normalized).getName()) : null;
				if (base != null)
				{
					if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: source trial 2='" + base.getAbsolutePath() + "' exists=" + base.exists());
					if (base.exists())
					{
						if (_log.isDebugEnabled()) _log.debug("[MJF-DBG] toURI: using sourcePath/baseName");
						return base.toURI();
					}
				}
			}

			// Fallback to original behavior: create a file:/// URI using package style
			final StringBuilder newUri = new StringBuilder();
			newUri.append("file:///");
			newUri.append(name.replace('.', '/'));
			if (name.endsWith(EXT))
				newUri.replace(newUri.length() - EXT.length(), newUri.length(), EXT);
			return URI.create(newUri.toString());
		}
		catch (Exception exp)
		{
			return URI.create("file:///com/sun/script/java/java_source");
		}
	}
}
