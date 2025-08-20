package com.kcube.cst.local;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.kcube.lib.repo.KcubeRepository;

/**
 * 스트링버퍼 테스트
 * @author ASUS
 */
public class Test
{
	public static void ListFile()
	{
		ListFile("/repository/kcube-repository");
	}

	// 특정 디렉토리의 파일이 존재하는 하위 폴더 포함 파일리스트 추출 재귀함수
	public static void ListFile(String strDirPath)
	{
		File path = new File(strDirPath);
		File[] fList = path.listFiles();

		for (int i = 0; i < fList.length; i++)
		{

			if (fList[i].isFile())
			{
				System.out.println(fList[i].getPath()); // 파일의 FullPath 출력
			}
			else if (fList[i].isDirectory())
			{
				ListFile(fList[i].getPath()); // 재귀함수 호출
			}
		}
	}
	// 출처:https:// sgpassion.tistory.com/331 [Experience !!:티스토리]

	public static void outBuffer()
	{
		StringBuffer a = new StringBuffer();
		for (int i = 0; i < 3; i++)
		{
			a.append("1");
			System.out.println(a);
			a.setLength(0);
		}
	}

	public static void inBuffer()
	{
		for (int i = 0; i < 3; i++)
		{
			StringBuffer a = new StringBuffer();
			a.append("1");
			System.out.println(a);
		}

	}

	// 바탕화면 startup들 삭제시 돌리면 됨.
	public static ArrayList<String> getName()
	{
		String tomcatPath = "D:\\tomcat";
		ArrayList<String> ArrList = new ArrayList<String>();
		try
		{
			File unzippedFile = new File(tomcatPath);
			File[] files = unzippedFile.listFiles();
			for (File f : files)
			{
				if (f.getName().startsWith("apache"))
				{
					ArrList.add(f.getName());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ArrList;
	}

	// 바탕화면 startup들 삭제시 돌리면 됨.
	public static void make()
	{
		ArrayList<String> arrList = getName();

		File file = null;

		for (String s : arrList)
		{
			file = new File(
				"C:\\Users\\ASUS\\Desktop\\startup"
					+ KcubeRepository.FILE_SEPARATOR
					+ s.substring(s.lastIndexOf("_") + 1)
					+ ".bat");

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file));)
			{
				bw.write("D:");
				bw.newLine();
				bw.write("cd D:\\tomcat\\" + s + "\\bin");
				bw.newLine();
				bw.write("catalina.bat run");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args)
	{
		// outBuffer();
		// System.out.println("----------------------------------------");
		// inBuffer();

		// getName();
		// make();

		// ListFile("/repository/kcube-repository");
	}

}
