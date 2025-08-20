// version update
var LANG_VERSION = '240619150115504';
var languageVer = localStorage.getItem('languageExtVer');
var scriptLocale = localStorage.getItem('scriptExtLocale');

// localStorage에 저장된 버전이 없거나 저장된 버전이 현재 LANG_VERSION과 다른 경우
// localStorage에 저장된 locale이 없거나 저장된 locale과 현재 사용자의 locale이 다른 경우 
if (languageVer == null || languageVer != LANG_VERSION || scriptLocale == null || scriptLocale != JSV.getLocale()) {
	JSV.cachedScript('/script.language.' + JSV.getLocale() + '.ext.js?nocache=' + LANG_VERSION, {
		'async' : false,
		'success' : function() {
			localStorage.setItem('languageExtVer', LANG_VERSION);
			localStorage.setItem('scriptExtLocale', JSV.getLocale());
		}
	});
}
