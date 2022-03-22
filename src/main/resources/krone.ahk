SetTitleMatchMode RegEx

<^#!s::
	SetTimer, Poll, 15000
	ToolTip, Script running
	SetTimer, RemoveToolTip, -1000
Return

<^#!e::
	SetTimer, Poll, Off
	ToolTip, Script halts
	SetTimer, RemoveToolTip, -1000
Return

Poll:
	If WinActive("ahk_exe i)\\chrome\.exe$")
	{
		Send, !s
		Sleep, 8000
		Send, ^a
		Send, ^c
		Send, {Esc}
	}
Return

RemoveToolTip:
	ToolTip
Return
