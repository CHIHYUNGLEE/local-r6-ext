package com.kcube.doc.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kcube.doc.Item;
import com.kcube.lib.compress.CompressionUtil;
import com.kcube.sys.AppException;

/**
 * 첨부파일을 Zip으로 묶어주는 Util
 */
public class AttachmentZipUtil
{
	private static final String KCUBECONTENTIMAGEHIDDEN = "KCUBECONTENTIMAGEHIDDEN";
	private static final String ZIP = "zip";
	public static final String ZIPEXT = ".zip";
	public static final String REMOVE = "remove";

	public static File getZipFile(Item server) throws Exception
	{
		return getZipFile(server, null, false);
	}

	/*
	 * zip 파일을 생성 한다.
	 */
	public static File getZipFile(Item server, Attachment att, boolean isOne) throws Exception
	{
		File destDir = null;
		try
		{
			destDir = File.createTempFile(ZIP, "");
			destDir.delete();
			destDir.mkdir();

			if (isOne && att != null)
			{
				handleFile(destDir, att);
			}
			else
			{
				handleFile(destDir, server);
			}
			if (destDir.list().length == 0)
			{
				throw new CompressFileNoneException();
			}
			return CompressionUtil.zip(destDir, false);
		}
		finally
		{
			if (destDir != null)
			{
				deleteDirectory(destDir);
			}
		}
	}

	/*
	 * 파일을 생성한다. 본문이미지로와 movie 제외한다.
	 */
	private static void handleFile(File destDir, Attachment att) throws Exception
	{
		if (att.getType() != null
			&& att.getType().intValue() != 7000
			&& att.getFilename() != null
			&& att.getFilename().indexOf(KCUBECONTENTIMAGEHIDDEN) != 0)
		{
			writeFile(destDir, att.getId(), att);
		}
	}

	/*
	 * 파일을 생성한다. 본문이미지로와 movie 제외한다.
	 */
	private static void handleFile(File destDir, Item server) throws Exception
	{
		try
		{
			for (Attachment att : server.getAttachments())
			{
				if (att.getType() != null
					&& att.getType().intValue() != 7000
					&& att.getFilename() != null
					&& att.getFilename().indexOf(KCUBECONTENTIMAGEHIDDEN) != 0)
				{
					writeFile(destDir, att.getId(), att);
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	/*
	 * 중복되지 않은 파일명 또는 폴더명을 돌려준다.
	 */
	private static String getName(File parent, String name)
	{
		Pattern p = Pattern.compile("(.*?)([(](\\d+)[)])?(\\..*)?");

		while (new File(parent, name).exists())
		{
			Matcher m = p.matcher(name);
			if (m.matches())
			{
				name = m.group(1)
					+ "("
					+ (m.group(2) == null ? 1 : (Integer.parseInt(m.group(3)) + 1))
					+ ")"
					+ (m.group(4) == null ? "" : m.group(4));
			}
		}
		return name;
	}

	/*
	 * 파일을 Temp 디렉토리에 생성한다.
	 */
	private static void writeFile(File destDir, Long id, Attachment att) throws Exception
	{
		OutputStream os = null;
		try
		{
			File file = new File(destDir, getName(destDir, att.getFilename()));
			os = new FileOutputStream(file);
			att.write(os);
		}
		finally
		{
			if (os != null)
			{
				os.close();
			}
		}
	}

	/*
	 * 디렉토리를 지운다.
	 */
	private static boolean deleteDirectory(File path)
	{

		if (!path.exists())
		{
			return false;
		}

		File[] files = path.listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				deleteDirectory(file);
			}
			else
			{
				file.delete();
			}
		}
		return path.delete();
	}

}

/**
 * 압축할 파일이 없으면 발생한다.
 */
class CompressFileNoneException extends AppException
{
	private static final long serialVersionUID = -3446321685005769908L;
}