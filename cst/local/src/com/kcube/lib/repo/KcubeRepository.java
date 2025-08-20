package com.kcube.lib.repo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kcube.sys.usr.UserService;

/**
 * File System에 첨부파일을 저장하고 내려준다.
 * <p>
 * File을 File System의 Root에 대한 상대경로로 처리한다. 추가 Disk 사용 시 File의 저장경로에는 "[KD1]0/3/4/abcd.gif,
 * [KD2]5/6/7/abcd.gif"와 같이 Disk Key가 추가된다. 다운로드 시 이 Key를 이용하여 File이 저장된 Disk를 구분할 수 있다.
 * <p>
 * <ul>
 * <li>totalDisks는 추가된 Disk들의 root directory를 의미한다.
 * <li>storeDisks는 Disk들중에서 파일저장이 가능한 disk들의 Key이다.
 * <li>tenantPath는 테넌트별로 파일을 구분해서 여부설정.
 * </ul>
 * 
 * <pre>
 * 예시
 * <bean id="KcubeRepository" class="com.kcube.lib.repo.KcubeRepository">
 *  <property name="tenantPath">
 *  	<value>false</value>
 *  </property>
 * 	<property name="width">
 * 		<value>10</value>
 * 	</property>
 * 	<property name="depth">
 * 		<value>3</value>
 * 	</property>
 * 	<property name="root">
 * 		<value>c:\kcube-repository</value>
 * 	</property>
 * 	<property name="totalDisks">
 * 		<map>
 * 			<entry key="root">
 * 				<value>c:\kcube-repository</value>
 * 			</entry>
 * 			<entry key="KD2">
 * 				<value>d:\kcube-repository</value>
 * 			</entry>
 * 			<entry key="KD3">
 * 				<value>d:\kcube-repo2</value>
 * 			</entry>
 * 		</map>
 * 	</property>
 * 	<property name="storeDisks">
 * 		<list>
 * 			<value>root</value>
 * 			<value>KD2</value>
 * 			<value>KD3</value>
 * 		</list>
 * 	</property>
 *	<property name="duplicationCheckDisks"> 중복문서제거 사용 시 스토리지 조건 포함여부 사용 시 적용되는 옵션
 *		<list>
 *			<value>root</value>
 *			<value>KD2</value>
 * 			<value>KD3</value>
 *		</list>
 *	</property>
 * </bean>
 * </pre>
 */
public class KcubeRepository implements Repository
{
	public static final String FILE_SEPARATOR = "/";

	private static Log _log = LogFactory.getLog(KcubeRepository.class);

	private String _root = "c:/kcube-repository";
	private Map<String, String> _totalDisks = new HashMap<String, String>();
	private List<String> _storeDisks = new LinkedList<String>();
	private List<String> _duplicationCheckDisks = new LinkedList<String>();

	private boolean _tenantPath = false;
	protected int _depth = 3;
	private int _width = 10;
	private String _prefix = System.getProperty("kcube.KcubeRepository.prefix");

	/**
	 * repository의 root를 설정한다.
	 */
	public void setRoot(String root)
	{
		_root = root;
	}

	/**
	 * repository의 root를 돌려준다.
	 */
	public String getRoot()
	{
		return _root;
	}

	/**
	 * 전체 Disk들을 설정한다.
	 */
	public void setTotalDisks(Map<String, String> totalDisks)
	{
		_totalDisks = totalDisks;
	}

	/**
	 * 확장 Disk들을 Map으로 돌려준다.
	 */
	public Map<String, String> getTotalDisks()
	{
		return _totalDisks;
	}

	/**
	 * Store 가능한 Disk들을 설정한다.
	 */
	public void setStoreDisks(List<String> storeDisks)
	{
		_storeDisks = storeDisks;
	}

	/**
	 * Store 가능한 Disk들을 List로 돌려준다.
	 */
	public List<String> getStoreDisks()
	{
		return _storeDisks;
	}

	/**
	 * Store 시 Disk별로 중복문서를 체크할 항목을 돌려준다.
	 * <p>
	 * DocumentConfig.isDisallowDuplication=true &
	 * DocumentConfig.isCheckStorageDuplication=true 일때 유효하다.
	 * <p>
	 * (duplicationCheckDisks의 disk가 없으면 DocumentConfig.isDisallowDuplication=true &
	 * DocumentConfig.isCheckStorageDuplication=false 와 동일하다.)
	 */
	public void setDuplicationCheckDisks(List<String> duplicationCheckDisks)
	{
		_duplicationCheckDisks = duplicationCheckDisks;
	}

	/**
	 * Store 가능한 Disk들을 List로 돌려준다.
	 */
	public List<String> getDuplicationCheckDisks()
	{
		return _duplicationCheckDisks;
	}

	/**
	 * 파일명의 Prefix 값을 설정한다.
	 */
	public void setPrefix(String prefix)
	{
		_prefix = prefix;
	}

	/**
	 * 저장경로에 tenant 정보를 포함할지 여부를 설정한다.
	 */
	public void setTenantPath(boolean tenantPath)
	{
		_tenantPath = tenantPath;
	}

	/**
	 * 저장경로에 tenant 정보를 포함할지 여부를 돌려준다.
	 */
	public boolean isTenantPath()
	{
		return _tenantPath;
	}

	/**
	 * hashing할 디렉토리의 깊이를 설정한다.
	 * <p>
	 * 양수가 아닌 경우 default 값인 3으로 설정된다.
	 */
	public void setDepth(int depth)
	{
		_depth = (depth > 0) ? depth : 3;
	}

	/**
	 * hashing할 디렉토리의 너비를 설정한다.
	 * <p>
	 * 양수가 아닌 경우 default 값이 10으로 설정된다.
	 */
	public void setWidth(int width)
	{
		_width = (width > 0) ? width : 10;
	}

	/**
	 * 해당 파일이 물리적으로 존재하는지 여부를 돌려준다.
	 */
	public boolean exist(String path) throws Exception
	{
		File src = getFile(path);
		return src.exists();
	}

	/**
	 * 해당 path의 파일을 OutputStream으로 출력한다.
	 */
	public HashMap<String, Object> write(String path, OutputStream os) throws Exception
	{
		return write(path, os, 0);
	}

	/**
	 * 해당 path의 파일을 주어진 offset 부터 OutputStream으로 출력한다.
	 */
	public HashMap<String, Object> write(String path, OutputStream os, long offset) throws Exception
	{
		if (_log.isDebugEnabled())
		{
			_log.debug("Loading " + path);
		}
		File src = getFile(path);
		return RepositoryService.copy(src, os, offset);
	}

	public HashMap<String, Object> write(String path, String dst) throws Exception
	{
		File src = getFile(path);
		return RepositoryService.copy(src, new File(dst));
	}

	/**
	 * 해당 path의 파일을 삭제한다.
	 */
	public void delete(String path) throws Exception
	{
		File src = getFile(path);
		boolean result = src.delete();
		if (!result)
		{
			if (_log.isWarnEnabled())
			{
				_log.warn("Cannot delete " + path);
			}
		}
		else if (_log.isDebugEnabled())
		{
			_log.debug("Deleted " + path);
		}
	}

	/**
	 * 다른 Repository에 저장되어 있는 파일을 복사하고, 복사된 경로를 돌려준다.
	 * <p>
	 * file의 path와 filesize가 설정된다.
	 * 
	 * <pre>
	 * KcubeRepository를 상속받아서 새로운 Repository를 구현한 경우
	 * Multipart Data 로 전달되어 write 된 File 객체를 가지고 Drm 등등의 작업을 하고자 할 경우에는
	 * 파일 크기가 10kbyte 초과 일 경우 아래[dst = getFile(path);]에 선언된 dst File 객체에는 data가 없으므로 다음과 같이 File 객체를 load
	 * 하여 작업하여야 한다.
	 * 
	 * if (os != null) {
	 * 	os.close();
	 * 	if (dst.length() == 0) {
	 * 		dst.delete();
	 * 		dst = getFile(file.getPath());
	 * 	}
	 * 	os = null;
	 * }
	 * 
	 * dst File 객체를 가지고 logic 처리
	 * File newDst = 처리 완료된 새로운 File
	 * path = 처리 완료되어 생성된 File 의 generatePath
	 * </pre>
	 */
	public String store(RepositoryFile file) throws Exception
	{
		String path = generatePath(file.getFilename());
		OutputStream os = null;
		File dst = null;
		try
		{
			dst = getFile(path);
			os = new FileOutputStream(dst);
			file.write(os);
		}
		finally
		{
			if (os != null)
			{
				os.close();
				if (dst.length() == 0)
				{
					dst.delete();
					path = null;
				}
			}
		}
		if (_log.isDebugEnabled())
		{
			_log.debug("Stored " + path);
		}
		return path;
	}

	/**
	 * path로 지정된 파일이 저장된 경로를 나타내는 실제 File 객체를 돌려준다.
	 * <p>
	 * Disk Key를 포함할 수 있다. 이 경우에는 totalDisks에서 disk의 root를 찾는다.
	 */
	protected File getFile(String path) throws Exception
	{
		if (StringUtils.contains(path, "..") || StringUtils.containsIgnoreCase(path, "file://"))
		{
			throw new KcubeRepositoryException.InvalidPath();
		}
		if (getRoot() == null)
		{
			throw new KcubeRepositoryException();
		}
		else
		{
			return new File(getRootPath(path), appendTenantPath(escapeDiskKey(path)));
		}
	}

	/**
	 * 파일이 저장된 Disk의 Root를 구하여 돌려준다.
	 * <p>
	 * Disk Root가 없으면 기본 root 경로를 돌려준다.
	 * @param path
	 */
	public String getRootPath(String path)
	{
		if (path.charAt(0) == '[')
		{
			int idx = path.indexOf("]");
			String diskRoot = getTotalDisks().get(path.substring(1, idx));
			return (diskRoot != null ? diskRoot : getRoot());
		}
		return getRoot();
	}

	/**
	 * path에서 Disk Key를 제거한다.
	 * @param path
	 */
	public String escapeDiskKey(String path)
	{
		if (path.charAt(0) == '[')
		{
			int idx = path.indexOf("]");
			return path.substring(idx + 1);
		}
		return path;
	}

	/**
	 * 신규 파일을 생성할 경로를 계산 후 실제 File 객체를 돌려준다.
	 */
	public File createFile(String diskPath, String path) throws Exception
	{
		if (diskPath == null)
		{
			throw new KcubeRepositoryException();
		}
		else
		{
			return new File(diskPath, path);
		}
	}

	/**
	 * 새로운 파일을 저장할 path를 생성한다.
	 */
	protected String generatePath(String filename) throws Exception
	{
		StringBuffer path = new StringBuffer();
		path.append(getPath());

		File dir = null;

		if (getStoreDisks() != null && getStoreDisks().size() > 0)
		{
			Random r = new Random();
			r.setSeed(System.currentTimeMillis());

			int diskIdx = r.nextInt(getStoreDisks().size());
			String diskKey = getStoreDisks().get(diskIdx);
			String diskPath = getTotalDisks().get(diskKey);

			dir = createFile(diskPath, appendTenantPath(path.toString()));

			path.insert(0, "[" + diskKey + "]");
		}
		else
		{
			dir = getFile(path.toString());
		}

		if (!dir.exists())
		{
			if (_log.isInfoEnabled())
			{
				_log.info("Creating repository " + dir.getAbsolutePath());
			}
			dir.mkdirs();
		}
		// unique한 filename을 받아오기 위해 임시파일을 생성한 후, 복사나 이동을 위해 다시 삭제한다.
		// cluster 환경이 아닌 경우에는 JVM을 restart하기 전에는 같은 이름의 파일이 생성되지 않으므로 삭제해도
		// 안전하다.
		File dst = File.createTempFile((_prefix != null) ? _prefix : "kcube", "", dir);
		dst.delete();

		int i = filename.lastIndexOf('.');
		String ext = (i < 0) ? "" : filename.substring(i);
		path.append(dst.getName()).append(ext);
		return path.toString();
	}

	/**
	 * 파일이 저장될 위치를 돌려준다.
	 */
	protected StringBuffer getPath()
	{
		StringBuffer path = new StringBuffer();

		Random r = new Random();
		r.setSeed(System.currentTimeMillis());

		for (int i = 0; i < _depth; i++)
		{
			path.append(r.nextInt(_width));
			path.append(FILE_SEPARATOR);
		}
		return path;
	}

	/**
	 * 파일의 날짜를 돌려준다.
	 * <p>
	 * 썸네일 삭제 등에 활용한다. crontab, job등에서 사용한다.
	 * @param path
	 * @throws Exception
	 */
	public Date getFiledate(String path) throws Exception
	{
		File file = getFile(path);
		long longdate = file.lastModified();
		return new Date(longdate);
	}

	/**
	 * 다양한 사이즈 썸네일 생성 또는 파일 존재확인에 활용한다.
	 * @param path
	 * @throws Exception
	 */
	public File getCreateFile(String path) throws Exception
	{
		return getFile(path);
	}

	/**
	 * 다양한 사이즈 썸네일 생성에 활용한다.
	 * @param path
	 * @throws Exception
	 */
	public String getCreatePath(String path) throws Exception
	{
		return generatePath(path);
	}

	/**
	 * tenantPath 추가
	 * @param path
	 * @return
	 */
	public String appendTenantPath(String path)
	{
		if (isTenantPath())
		{
			StringBuffer str = new StringBuffer();
			str.append('t');
			str.append(UserService.getTenantId());
			str.append(FILE_SEPARATOR);
			str.append(path);
			return str.toString();
		}
		else
			return path;
	}
}