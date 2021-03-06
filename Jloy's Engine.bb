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
; - Mark "Cor�E (mabc_bh at yahoo dot com dot br)
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
;               17/01/2008 - Code reorganization.                                                              ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO:                                                                                                      ;
;                                                                                                              ;
;==============================================================================================================;

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   STARTUP
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; --- Include critical libraries before starting ----
; Code code
Include "_SourceCode\Core\Core_GeneralConstants.bb"
Include "_SourceCode\Core\Core_InputManagement.bb"

; Libraries code
Include "_SourceCode\Libraries\Library_FastImage.bb"
Include "_SourceCode\Libraries\Library_XMLParser.bb"
Include "_SourceCode\Systems\System_FxManager.bb"

; Game code
Include "_SourceCode\Game\Game_Settings.bb"

; --- Initializate 3D mode ---
Game_LoadConfig()
Graphics3D(GAME_WINDOW_W, GAME_WINDOW_H, GAME_WINDOW_DEPTH, GAME_WINDOW_MODE)
Dither(False)
WBuffer(True)
AntiAlias(False)
SetBuffer(BackBuffer())
HidePointer()

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   INCLUDES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; Core code
Include "_SourceCode\Core\Core_DeltaTime.bb"
Include "_SourceCode\Core\Core_Maths.bb"
Include "_SourceCode\Core\Core_Tools.bb"

; Game code
Include "_SourceCode\Game\Game.bb"
Include "_SourceCode\Game\Game_Interface.bb"
Include "_SourceCode\Game\Game_Resources_Models.bb"
Include "_SourceCode\Game\Game_Resources_Textures.bb"
Include "_SourceCode\Game\Game_Resources_Sounds.bb"

; Stage code
Include "_SourceCode\Game\Stage\Stage.bb"
Include "_SourceCode\Game\Stage\Camera\Camera.bb"
Include "_SourceCode\Game\Stage\Player\Player.bb"
Include "_SourceCode\Game\Stage\Player\Player_Management.bb"
Include "_SourceCode\Game\Stage\Player\Player_Motion.bb"
Include "_SourceCode\Game\Stage\Player\Player_Animation.bb"
Include "_SourceCode\Game\Stage\Objects\Objects.bb"

; Menu code
Include "_SourceCode\Game\Menu\Menu.bb"
Include "_SourceCode\Game\Menu\Menu_Machine.bb"
Include "_SourceCode\Game\Menu\Menu_Interface.bb"
Include "_SourceCode\Game\Menu\Menu_Control_Text.bb"
Include "_SourceCode\Game\Menu\Menu_Control_Image.bb"

; General systems code
Include "_SourceCode\Systems\System_FxManager.bb"
Include "_SourceCode\Systems\System_PostFX.bb"
Include "_SourceCode\Systems\System_GameScript_VM.bb"
Include "_SourceCode\Systems\System_GameScript_Compiler.bb"
Include "_SourceCode\Systems\System_GameScript_Functions.bb"

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   MENU OBJECTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; The stage list type
Type tStageList
	Field Name$
	Field Folder$
	Field ID
End Type
Global StageIDCounter = 0

Global SelectedStage$
Global SelectedCharacter

; Different menu screens
Const MENU_INTRO		= -1
Const MENU_TITLE 		= 0
Const MENU_MAIN 		= 1
Const MENU_CHARSEL 		= 2
Const MENU_STAGESEL		= 3
Const MENU_GAMELOOP 	= 4
Const MENU_IGNOREARG	= 999 ;This is special in the stage list loading.
	
; Current menu screen
Global menuState 		= MENU_INTRO

; Intro screen controls
Global IntroLogo				= LoadImage("Interface\Menu\JloyLogo.png")
Global MilliStart				= MilliSecs()
Global CurrentMilli				= MilliSecs()
Global IntroPlaySoundTrigger 	= 1500 ; Trigger after x ms.
Global IntroSoundTriggered		= False

; Used to navigate menus
Global menuPosition		= 0
Global menuMaxPos		= 0

; Load our text image
Global menuText01	= LoadAnimImage("Interface\Menu\TitleFont.png", 23, 17, 0, 39)
Global menuCursor	= LoadAnimImage("Interface\Menu\MenuCursor.png", 20, 15, 0, 2)

; Mask all our images
MaskImage(menuText01, 255, 0, 255)
MaskImage(menuCursor, 255, 0, 255)
MaskImage(IntroLogo,  255, 0, 255)

; Scale if we need to after the masking.
ScaleImage(IntroLogo, 2, 2)
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   ENTRY POINT
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
	
	; Preload the needed tools to render objects
	preLoad()
	
	Function preLoad()
		; Set game title
		AppTitle(GAME_TITLE$)
		WindowHWND = SystemProperty("AppHWND")
		InitDraw(SystemProperty("Direct3DDevice7"))
		
		; Enter the main loop
		mainLoop()	
	End Function
	
	Function mainLoop()
		While(1)
			Select (menuState)
				Case MENU_INTRO
					Menu_MenuIntro()
				Case MENU_TITLE
					Menu_MenuTitle()
				Case MENU_MAIN
					Menu_MenuMain()
				Case MENU_CHARSEL
					Menu_CharacterSelect()
				Case MENU_STAGESEL
					Menu_StageSelect()
				Case MENU_GAMELOOP
					gameRunLoop()
				Default	
					RuntimeError("Unknown menu state.")
			End Select
		Wend	
		End
	End Function

	Function preLoadGame()
		; Runs the Game_Startup an initiates the game loop.
		Game_Startup()
		menuState = MENU_GAMELOOP
	End Function

	Function gameRunLoop()
		If (KeyHit(KEY_ESCAPE) And Input_Lock = False) Then End ; Temporary to exit game
		Game_Update()
		If (KeyHit(KEY_P)) Then FlushKeys : Game_End()
	End Function

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   MENU SCREENS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/	
	
	; =========================================================================================================
	; Menu_MenuIntro
	; =========================================================================================================
	Function Menu_MenuIntro()
		; Clear the screen
		Cls()
		
		; Update current time.
		CurrentMilli = MilliSecs()
		
		; Draw our logo.
		DrawImage(IntroLogo, (GAME_WINDOW_W/2)-140, (GAME_WINDOW_H/2)-80)
		
		; Has the sound played? If not, play it when needed.
		If ((IntroSoundTriggered = False) And (CurrentMilli - MilliStart >= IntroPlaySoundTrigger)) Then
			IntroSoundTriggered = True
			PlaySound(Sound_Ring)
		End If

		; If intro played for Xms, go to title
		If (CurrentMilli - MilliStart >= IntroPlaySoundTrigger*2) Then
			menuState = MENU_TITLE
		End If
		
		; Flip the buffer
		Flip()
	End Function
	
	; =========================================================================================================
	; Menu_MenuTitle
	; =========================================================================================================
	Function Menu_MenuTitle()		; Clear the screen
		Cls()
		
		; Draw the background first
		; <BACKGROUND IMAGE GOES HERE>
		
		; Next, render the text
		RenderMenuText(GAME_WINDOW_W/2, 4, "Jloy's Test Engine", True)
		If ((MilliSecs() Mod 1000) <= 500) Then RenderMenuText(GAME_WINDOW_W/2, GAME_WINDOW_H-24, "Press Enter to begin", True)

		; Listen to the enter key and start game if hit
		If (KeyHit(KEY_ENTER)) Then menuState = MENU_MAIN
		
		; Flip the buffer
		Flip()
	End Function
	
	
	; =========================================================================================================
	; Menu_MenuMain
	; =========================================================================================================
	Function Menu_MenuMain()
		; Set the max position of this menu
		menuMaxPos = 2
		
		; Listen for the input
		PerformMenuInput(0)
		
		; Clear the screen
		Cls()
		
		; Draw the background first
		; <BACKGROUND IMAGE GOES HERE>
		
		; Next, render the text
		RenderMenuText(GAME_WINDOW_W/2, 4, "Select an option", True)
		Select (menuPosition)
			Case 0
				RenderMenuText(GAME_WINDOW_W/2, 64, "Sandbox", True, True)
				RenderMenuText(GAME_WINDOW_W/2, 84, "Options", True)
				RenderMenuText(GAME_WINDOW_W/2, 104, "Exit", True)
			Case 1
				RenderMenuText(GAME_WINDOW_W/2, 64, "Sandbox", True)
				RenderMenuText(GAME_WINDOW_W/2, 84, "Options", True, True)
				RenderMenuText(GAME_WINDOW_W/2, 104, "Exit", True)
			Case 2
				RenderMenuText(GAME_WINDOW_W/2, 64, "Sandbox", True)
				RenderMenuText(GAME_WINDOW_W/2, 84, "Options", True)
				RenderMenuText(GAME_WINDOW_W/2, 104, "Exit", True, True)
		End Select	
		
		; Listen for the selection
		If (KeyHit(KEY_ENTER)) Then 
			Select (menuPosition)
				Case 0
					menuState = MENU_CHARSEL
				Case 1
					PlaySound(Sound_Ring)
				Case 2
					PlaySound(Sound_Ring)
			End Select
		End If
		
		; Flip the buffer
		Flip()
	End Function


	; =========================================================================================================
	; Menu_CharacterSelect
	; =========================================================================================================
	Function Menu_CharacterSelect()
		; Set the max position of this menu
		menuMaxPos = 2
		
		; Listen for the input
		PerformMenuInput(1)
		
		; Clear the screen
		Cls()
		
		; Draw the background first
		; <BACKGROUND IMAGE GOES HERE>
		
		; Next, render the text
		RenderMenuText(GAME_WINDOW_W/2, 4, "Select a character", True)
		Select (menuPosition)
			Case 0
				RenderMenuText(GAME_WINDOW_W/2, GAME_WINDOW_H-40, "Sonic", True)
			Case 1
				RenderMenuText(GAME_WINDOW_W/2, GAME_WINDOW_H-40, "Amy", True)
			Case 2
				RenderMenuText(GAME_WINDOW_W/2, GAME_WINDOW_H-40, "Tails", True)
		End Select	
		
		; Listen for the selection
		If (KeyHit(KEY_ENTER)) Then 
			Select (menuPosition)
				Case 0 ;Sonic
					SelectedCharacter = CHARACTER_SONIC
					LoadStageList()
				Case 1 ; Amy
					PlaySound(Sound_Ring)
				Case 2 ; Tails
					PlaySound(Sound_Ring)
			End Select
		End If
		
		; Flip the buffer
		Flip()
	End Function

	; =========================================================================================================
	; Menu_StageSelect
	; =========================================================================================================
	Function Menu_StageSelect()
		; Set the max position of this menu
		menuMaxPos = StageIDCounter-1
		
		; Listen for the input
		PerformMenuInput(0)
		
		; Clear the screen
		Cls()
		
		; Draw the background first
		; <BACKGROUND IMAGE GOES HERE>
		
		; Next, render the text
		RenderMenuText(GAME_WINDOW_W/2, 4, "Select a stage", True)
		For stg.tStageList = Each tStageList
			If (menuPosition = stg\ID) Then
				RenderMenuText(60, 46+(stg\ID*21), stg\Name$, False, True)
			Else
				RenderMenuText(60, 46+(stg\ID*21), stg\Name$, False)
			End If
		Next
		
		; Listen for the selection
		If (KeyHit(KEY_ENTER)) Then 
			For stg.tStageList = Each tStageList
				; If the ID is equal to the StageList ID, then we're good to go.
				If (menuPosition = stg\ID) Then
					SelectedStage$ = stg\Folder$
				End If
			Next
			
			; Now unload the stage list and start the game.
			UnloadStageList()
			preLoadGame()
		End If
		
		; Flip the buffer
		Flip()
	End Function

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   MENU INPUT LISTENER
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/		
	; =========================================================================================================
	; PerformMenuInput
	; =========================================================================================================
	; TODO: Allow for user configurable keys to trigger
	; =========================================================================================================
	Function PerformMenuInput(inputStyle=0)
		Select (inputStyle)
			Case 0 ; UP DOWN STYLE
				If KeyHit(KEY_ARROW_UP) Then
					If (menuPosition = 0) Then
						menuPosition = menuMaxPos
					Else
						menuPosition = menuPosition - 1
					End If
				End If				
				If KeyHit(KEY_ARROW_DOWN) Then
					If (menuPosition = menuMaxPos) Then
						menuPosition = 0
					Else
						menuPosition = menuPosition + 1
					End If
				End If
				
			Case 1 ; LEFT RIGHT STYLE
				If KeyHit(KEY_ARROW_LEFT) Then
					If (menuPosition = 0) Then
						menuPosition = menuMaxPos
					Else
						menuPosition = menuPosition - 1
					End If
				End If				
				If KeyHit(KEY_ARROW_RIGHT) Then
					If (menuPosition = menuMaxPos) Then
						menuPosition = 0
					Else
						menuPosition = menuPosition + 1
					End If
				End If				
		End Select
	End Function
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   RENDER MENU TEXT
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/		
	; =========================================================================================================
	; RenderMenuText
	; =========================================================================================================
	Function RenderMenuText(x, y, textString$, centreAlign=False, drawArrow=False, textTileOffset=0)
		; Get the string and gather the tiles to render
		If (centreAlign = True) Then 
			xStart = x - (Len(textString$) * (9 + textTileOffset))
			x = xStart
		Else
			xStart = x + 18
		End If
		
		; Now go through each letter and select the tile to draw.
		For i = 1 To Len(textString$)
			AscNum = Asc(Mid$(textString$, i, 1))
			; If the tile isn't a space (blank tile)
			If Not (AscNum = 32) Then
				; Select the tile using the offset.
				If ((AscNum >= 48) And (AscNum <= 57))  Then DrawImage(menuText01, x, y, Asc(Mid$(textString$, i, 1))-22)
				If ((AscNum >= 65) And (AscNum <= 90))  Then DrawImage(menuText01, x, y, Asc(Mid$(textString$, i, 1))-65)
				If ((AscNum >= 97) And (AscNum <= 122)) Then DrawImage(menuText01, x, y, Asc(Mid$(textString$, i, 1))-97)
				If (AscNum = 39) Then DrawImage(menuText01, x, y, 36)
				If (AscNum = 58) Then DrawImage(menuText01, x, y, 37)
			End If
			
			; Add to the next offset. 
			x = x + 18 + textTileOffset
		Next
		
		; Yay, arrows for showing selection.
		If (drawArrow = True) Then
			DrawImage(menuCursor, xStart-40, y+1, 0)
			DrawImage(menuCursor, x+20, y+1, 1)
		End If
	End Function
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   EXTRA FUNCTIONS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/		
	; =========================================================================================================
	; LoadStageList
	; =========================================================================================================
	Function LoadStageList(changeToMenuState=MENU_STAGESEL)
		; This selects the XML file to read.
		StageListRoot = xmlLoad("Stages/Stages.xml")
		
		; If there is any errors, execute a RuntimeError.
		If (xmlErrorCount()>0) Then RuntimeError("An error while parsing the level list! Make sure the XML file is properly formatted. Location: 'Stages/Stages.xml'")

		; For each node, populate a new node.
		For i=1 To xmlNodeChildCount(StageListRoot)
			Child = xmlNodeChild(StageListRoot, i)
			
			; We found a stage. Load it.
			If (xmlNodeNameGet(Child)="stage") Then
				stg.tStageList = New tStageList
				stg\Name$ = xmlNodeAttributeValueGet(Child, "name")
				stg\Folder$ = xmlNodeAttributeValueGet(Child, "folder")
				stg\ID = StageIDCounter
				StageIDCounter = StageIDCounter + 1
			End If 
		Next
		xmlNodeDelete(StageListRoot)
		
		If (changeToMenuState = MENU_IGNOREARG) Then 
			; This cannot ignore args, so force it to default if selected.
			menuState = MENU_STAGESEL
		Else
			menuState = changeToMenuState
		End If
	End Function
	
	; =========================================================================================================
	; UnloadStageList
	; =========================================================================================================
	Function UnloadStageList(changeToMenuState=MENU_IGNOREARG)
		; Delete all StageList entries.
		For stg.tStageList = Each tStageList
			Delete stg
		Next
		StageIDCounter = 0
		If (changeToMenuState <> MENU_IGNOREARG) Then menuStage = changeToMenuState
	End Function	
	
;~IDEal Editor Parameters:
;~C#Blitz3D