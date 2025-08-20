package com.kcube.cst.local;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 주의사항 :
 * <p>
 * 1. 최신 커밋된 소스만 가능,,,
 * <P>
 * 2. 로그의 위치가 바탕화면인게 디폴트임 바뀔경우 defaultPath 변경.
 * <p>
 * 3. 프로젝트에 따라서 projectName 변경.
 * <P>
 * 사용법 : 바탕화면에 changes.log과 소스 extract 한 후 main메소드 실행.
 * @author ASUS
 */
public class PatchShell
{
	// 내 로컬의 change로그 위치. 다른곳에다가 저장했으면 변경 必.
	public static final String defaultPath = "C:\\Users\\ASUS\\Desktop\\";

	// 체인지 로그 이름.
	public static final String log = "changes.log";
	public static final String dirPath = "$dirPath";
	public static LocalDate ld = LocalDate.now();
	public static String year = String.valueOf(ld.getYear());
	public static String month = ld.getMonthValue() < 10
		? "0" + String.valueOf(ld.getMonthValue())
		: String.valueOf(ld.getMonthValue());
	public static String day = ld.getDayOfMonth() < 10
		? "0" + String.valueOf(ld.getDayOfMonth())
		: String.valueOf(ld.getDayOfMonth());
	public static String date = year + month + day;
	public static String backUpSuffix = "_" + date + "_bak";

	/**
	 * 백업쉘 생성
	 * @throws Exception
	 */
	public static void backUpCreate() throws Exception
	{
		File logFile = new File(defaultPath + log);
		File patchLog = new File(defaultPath + "backup_" + date + ".sh");
		try (FileReader fr = new FileReader(logFile);
			BufferedReader br = new BufferedReader(fr);
			BufferedWriter bw = new BufferedWriter(new FileWriter(patchLog));)
		{
			String line = "";

			// 쉘스크립트 기본
			bw.write("#!/bin/sh");
			bw.newLine();
			bw.write("dirPath=`dirname $0`");
			bw.newLine();
			// 패치파일 생성
			while ((line = br.readLine()) != null)
			{
				int idxM = line.indexOf("\\");

				if (idxM > -1)
				{
					Map<String, String> versionMap = versionExt(line);
					String version = versionMap.get("version");
					String projectName = versionMap.get("projectName");
					line = line.substring(18).replaceAll("\\\\", "/");
					// String filename = line.substring(line.lastIndexOf("/") + 1);
					String filePath = line.substring(0, line.lastIndexOf("/") + 1);
					int idx$ = line.indexOf("$");
					if (idx$ > -1)
					{
						line = line.replaceAll("\\$", java.util.regex.Matcher.quoteReplacement("'$'"));
					}
					String nowFilePath = dirPath + line;
					bw.write("mv -f " + nowFilePath + " " + nowFilePath + backUpSuffix);
					bw.newLine();
					if (versionMap.get("version").equals("5"))
					{
						bw.write(
							"cp -f "
								+ nowFilePath
								+ backUpSuffix
								+ " "
								+ dirPath
								+ "/source/"
								+ projectName
								+ filePath);
					}
					else if (version.equals("6"))
					{
						bw.write(
							"cp -f "
								+ nowFilePath
								+ backUpSuffix
								+ " "
								+ dirPath
								+ "/source2/"
								+ projectName
								+ filePath);
					}
					bw.newLine();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 패치 쉘 생성
	 * @throws Exception
	 */
	public static void patchCreate() throws Exception
	{
		File logFile = new File(defaultPath + log);
		File patchLog = new File(defaultPath + "patch_" + date + ".sh");
		try (FileReader fr = new FileReader(logFile);
			BufferedReader br = new BufferedReader(fr);
			BufferedWriter bw = new BufferedWriter(new FileWriter(patchLog));)
		{
			String line = "";

			// 쉘스크립트 기본
			bw.write("#!/bin/sh");
			bw.newLine();
			bw.write("dirPath=`dirname $0`");
			bw.newLine();

			// 패치파일 생성
			while ((line = br.readLine()) != null)
			{
				int idx = line.indexOf("M");

				if (idx > -1)
				{
					Map<String, String> versionMap = versionExt(line);
					String version = versionMap.get("version");
					String projectName = versionMap.get("projectName");
					line = line.substring(18).replaceAll("\\\\", "/");
					if (version.equals("5"))
					{
						bw.write("cp -f " + dirPath + "/source/" + projectName + line + " " + dirPath + line);
					}
					else if (version.equals("6"))
					{
						bw.write("cp -f " + dirPath + "/source2/" + projectName + line + " " + dirPath + line);
					}
					bw.newLine();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 버전 추출
	 * @param line
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> versionExt(String line) throws Exception
	{
		String projectName = line.substring(line.indexOf("\\") + 1, line.indexOf("\\", line.indexOf("\\") + 1));
		String version = projectName.substring(1, 2);
		Map<String, String> strArray = new HashMap<String, String>();
		strArray.put("projectName", projectName);
		strArray.put("version", version);
		return strArray;
	}

	public static void main(String[] args) throws Exception
	{
		try
		{
			backUpCreate();
			patchCreate();
			System.out.println("");
			System.out.println("------------------------file create success!!");
		}
		catch (Exception e)
		{
			System.out.println("file create fail!!");
			e.printStackTrace();
		}
	}

}
