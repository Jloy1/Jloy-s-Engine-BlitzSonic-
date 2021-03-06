; ------------------------------------------------------------------------
; BlitzSonic Engine -- Classic Sonic the Hedgehog engine for Blitz 3D
; version 0.1, February 7th, 2008
;
; Copyright (C) 2008 - BlitzSonic Team.
; ------------------------------------------------------------------------
;
; This software is provided 'as-is', without any express or implied
; warranty.  In no event will the authors be held liable for any damages
; arising from the use of this software.
; 
; Permission is granted to anyone to use this software for any purpose
; (except for commercial applications) and to alter it and redistribute
; it freely subject to the following restrictions:
;
; 1. The origin of this software must not be misrepresented; you must not
;    claim that you wrote the original software. If you use this software
;    in a product, an acknowledgment in the product itself as a splash
;    screen is required.
; 2. Altered source versions must be plainly marked as such, and must not be
;    misrepresented as being the original software.
; 3. This notice may not be removed or altered from any source distribution.
;
; All characters and materials in relation to the Sonic the Hedgehog game series
; are copyrights/trademarks of SEGA of Japan (SEGA Co., LTD). This product
; has been developed without permission of SEGA, therefore it's prohibited
; to sell/make profit of it.
;
; The BlitzSonic Team:
; - H�ctor "Damizean" (elgigantedeyeso at gmail dot com)
; - Mark "Cor�" (mabc_bh at yahoo dot com dot br)
; - Streak Thunderstorm
; - Mista ED
;

;\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/;
;	Project Title : Sonic the Hedgehog                                                                         ;
; ============================================================================================================ ;
;	Author :                                                                                                   ;
;	Email :                                                                                                    ;
;	Version: 0.1                                                                                               ;
;	Date: --/--/2008                                                                                           ;
;                                                                                                              ;
;\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/;
;                                                                                                              ;
;   Changelog:  -(Damizean)------------------------------->                                                    ;
;               20/01/2008 - Started working on Menu engine.                                                   ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO: - Implement pretty much everything                                                                   ;
;                                                                                                              ;
;==============================================================================================================;

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\
; 	STRUCTURES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\

	; ---------------------------------------------------------------------------------------------------------
	; tMenu_Interface
	; ---------------------------------------------------------------------------------------------------------
	Type tMenu_Interface
		Field Name$
		Field NumControls
		Field Controls.tMenu_Control[MENU_INTERFACE_MAXCONTROLS]
	End Type


	; ---------------------------------------------------------------------------------------------------------	
	; tMenu_Control
	; ---------------------------------------------------------------------------------------------------------
	Type tMenu_Control
		Field Name$
		Field Kind
		Field Addr%
		Field Visible
	End Type
	
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	CONSTANTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; Interface 
	Const MENU_INTERFACE_MAXCONTROLS 	= 100

	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	METHODS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---- Creation/Destruction and management ----
	; =========================================================================================================
	; Menu_Interface_Create
	; =========================================================================================================
	Function Menu_Interface_Create.tMenu_Interface(Name$)
		i.tMenu_Interface = New tMenu_Interface
			i\Name$ = Name$

		Return i
	End Function


	; =========================================================================================================
	; Menu_Interface_AttachControl
	; =========================================================================================================
	Function Menu_Interface_AttachControl(i.tMenu_Interface, c.tMenu_Control)
		i\Controls[i\NumControls] = c
		i\NumControls = i\NumControls+1
	End Function


	; =========================================================================================================
	; Menu_Interface_DetachControl
	; =========================================================================================================
	Function Menu_Interface_DetachControl(i.tMenu_Interface, c.tMenu_Control)
		; Search for the control
		For j=0 To i\NumControls-1
			If (i\Controls[j]=c) Then
				Menu_Interface_DetachControlById(i, j)
				Return
			End If
		Next
	End Function


	; =========================================================================================================
	; Menu_Interface_DetachControlById
	; =========================================================================================================
	Function Menu_Interface_DetachControlById(i.tMenu_Interface, id)
		If (id < 0 Or id >= i\NumControls) Then Return
		
		For j = id+1 To i\NumControls-1
			i\Controls[j-1] = i\Controls[j]
		Next
		i\Controls[i\NumControls-1] = Null
		i\NumControls = i\NumControls-1
	End Function


	; ---- General control management -----
	; =========================================================================================================
	; Menu_Interface_SearchControl
	; =========================================================================================================
	Function Menu_Interface_SearchControl.tMenu_Control(i.tMenu_Interface, Name$)
		For j = 0 To i\NumControls-1
			If (i\Controls[j]\Name$ = Name$) Then Return i\Controls[j]
		Next
		Return Null
	End Function


	; =========================================================================================================
	; Menu_Interface_SearchControlAndRetrieveID
	; =========================================================================================================
	Function Menu_Interface_SearchControlAndRetrieveID(i.tMenu_Interface, Name$)
		For j = 0 To i\NumControls-1
			If (i\Controls[j]<>Null) Then
				If (i\Controls[j]\Name$ = Name$) Then Return j
			End If
		Next
		Return -1
	End Function


	; =========================================================================================================
	; Menu_Control_GetName
	; =========================================================================================================
	Function Menu_Interface_GetName(c.tMenu_Control)
		Return c\Name$
	End Function


	; =========================================================================================================
	; Menu_Control_GetKind
	; =========================================================================================================
	Function Menu_Control_GetKind(c.tMenu_Control)
		Return c\Kind
	End Function


	; =========================================================================================================
	; Menu_Control_GetAddress
	; =========================================================================================================
	Function Menu_Control_GetAddress(c.tMenu_Control)
		Return c\Addr
	End Function


	; =========================================================================================================
	; Menu_Control_IsVisible
	; =========================================================================================================
	Function Menu_Control_IsVisible(c.tMenu_Control)
		Return c\Visible
	End Function


	; =========================================================================================================
	; Menu_Control_SetName
	; =========================================================================================================
	Function Menu_Control_SetName(c.tMenu_Control, Name$)
		c\Name$ = Name$
	End Function


	; =========================================================================================================
	; Menu_Control_SetKind
	; =========================================================================================================
	Function Menu_Control_SetKind(c.tMenu_Control, Kind)
		c\Kind = Kind
	End Function


	; =========================================================================================================
	; Menu_Control_SetAddress
	; =========================================================================================================
	Function Menu_Control_SetAddress(c.tMenu_Control, Addr%)
		c\Addr% = Addr%
	End Function


	; =========================================================================================================
	; Menu_Control_Show
	; =========================================================================================================
	Function Menu_Control_Show(c.tMenu_Control)
		c\Visible = True
	End Function


	; =========================================================================================================
	; Menu_Control_Hide
	; =========================================================================================================
	Function Menu_Control_Hide(c.tMenu_Control)
		c\Visible = False
	End Function