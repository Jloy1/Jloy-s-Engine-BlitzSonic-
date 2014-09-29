.lib "kernel32.dll"

api_OpenFile% (lpFileName$, lpReOpenBuff*, wStyle%) : "OpenFile"
api_GetFileTime% (hFile%, lpCreationTime*, lpLastAccessTime*, lpLastWriteTime*) : "GetFileTime"
api_FileTimeToLocalFileTime% (lpFileTime*, lpLocalFileTime*) : "FileTimeToLocalFileTime"
api_FileTimeToSystemTime% (lpFileTime*, lpSystemTime*) : "FileTimeToSystemTime"
api_CloseHandle% (hObject%) : "CloseHandle"

.lib "user32.dll"
api_GetFocus%() : "GetFocus"
api_GetForegroundWindow%() : "GetForegroundWindow"