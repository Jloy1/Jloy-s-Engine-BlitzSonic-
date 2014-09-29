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
; - Héctor "Damizean" (elgigantedeyeso at gmail dot com)
; - Mark "Coré" (mabc_bh at yahoo dot com dot br)
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
;               19/01/2008 - Code reorganization.                                                              ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO:                                                                                                      ;
;                                                                                                              ;
;==============================================================================================================;

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	CONSTANTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; --- Collisions constants ---
	Const COLLISION_NONE				=	0
	Const COLLISION_PLAYER				=	1
	Const COLLISION_CAMERA				=	2
	
	Const COLLISION_WORLD_POLYGON		=	3
	Const COLLISION_WORLD_POLYGON_ALIGN	=	4
	Const COLLISION_WORLD_BOX			=	5

	Type MeshStructure
		Field Entity
	End Type

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
; 	METHODS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; ---------------------------------------------------------------------------------------------------------
	; Game_Stage_Update
	; ---------------------------------------------------------------------------------------------------------
	Function Game_Stage_Update()
		Select Game\State
			Case GAME_STATE_START : Game_Stage_Start()
			Case GAME_STATE_STEP  : Game_Stage_Step()
			Case GAME_STATE_END   : Game_Stage_End()
		End Select
	End Function

	; ---------------------------------------------------------------------------------------------------------
	; Game_Stage_Start
	; ---------------------------------------------------------------------------------------------------------
	Function Game_Stage_Start()
		; At startup, load up current stage from the list. First, obtain the final path to the stage folder
		; and hold on a string. Then, parse the Stage.xml file to find out all the specifications
		; and objects on the stage.
		Path$		= "Stages/"+Game\Stage\List\Folder$+"/"

		; Create root entity for all the stage entities
		Game\Stage\Root 			= CreatePivot()
		Game\Stage\Gravity			= CreatePivot()
		Game\Stage\GravityAlignment	= Vector(0, 1, 0)

		; Create camera
		camera.tCamera = Camera_Create()
		
		; Parse up Stage.xml file and retrieve root node. Find out if any parse errors ocurred while loading
		; and if so, stop the game.
		Game_Stage_UpdateProgressBar("Parsing stage XML", 0)
		RootNode	= xmlLoad(Path$+"Stage.xml")
		If (xmlErrorCount()>0) Then RuntimeError("Game_Update_Stage() -> [Start] Error loading stage '"+Game\Stage\List\Folder$+"' xml. Parse error.")

		; Retrieve stage settings. From here on, this programming is kinda shitty, as the program presumes
		; all the nodes we're going to access to exist.
		For i = 1 To xmlNodeChildCount(RootNode)
			; Get child node
			RootChildNode = xmlNodeChild(RootNode, i)
	
			; Find out wich type it is.
			Select xmlNodeNameGet$(RootChildNode)
				Case "information"
					
					; Retrieve stage information such as stage's name, music and ambient light.
					For j = 1 To xmlNodeChildCount(RootChildNode)
						; Update progress bar
						Game_Stage_UpdateProgressBar("Loading stage information", Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
						
						; Retrieve child node
						InformationChildNode = xmlNodeChild(RootChildNode, j)
		
						; Find out what it is and acquire.
						Select xmlNodeNameGet$(InformationChildNode)
							Case "name"
								Game\Stage\Properties\Name$ = xmlNodeDataGet(InformationChildNode)
							Case "music"
								Game\Stage\Properties\Music = LoadSound("Music/"+xmlNodeDataGet(InformationChildNode))
								LoopSound(Game\Stage\Properties\Music)
							Case "ambientlight"
								AmbientLight(xmlNodeAttributeValueGet(InformationChildNode, "r"), xmlNodeAttributeValueGet(InformationChildNode, "g"), xmlNodeAttributeValueGet(InformationChildNode, "b"))
							Case "skybox"
								Game\Stage\Properties\SkyBox = LoadMesh(Path$+xmlNodeDataGet(InformationChildNode))
								ScaleEntity(Game\Stage\Properties\SkyBox, 0.1, 0.1, 0.1)
								EntityColor(Game\Stage\Properties\SkyBox, 255, 255, 255)
								EntityFX(Game\Stage\Properties\SkyBox, 9)										
								EntityOrder(Game\Stage\Properties\SkyBox, 1)
							Case "water"
								If xmlNodeAttributeValueGet(InformationChildNode, "on") = 1 Then
									WaterMesh$   = xmlNodeAttributeValueGet(InformationChildNode, "mesh")
									WaterScaleX$ = xmlNodeAttributeValueGet(InformationChildNode, "scale_x")
									WaterScaleY$ = xmlNodeAttributeValueGet(InformationChildNode, "scale_y")
									WaterScaleZ$ = xmlNodeAttributeValueGet(InformationChildNode, "scale_z")
									WaterTexture$ = xmlNodeAttributeValueGet(InformationChildNode, "texture")
									
									If (WaterMesh$ <> "") Then
										Game\Stage\Properties\Water = LoadMesh(Path$+WaterMesh$)
										If (WaterScaleX$ <> "") Then ScaleEntity(Game\Stage\Properties\Water, Float#(WaterScaleX$), BB_EntityScaleY#(Game\Stage\Properties\Water), BB_EntityScaleZ#(Game\Stage\Properties\Water))
										If (WaterScaleY$ <> "") Then ScaleEntity(Game\Stage\Properties\Water, BB_EntityScaleX#(Game\Stage\Properties\Water), Float#(WaterScaleY$), BB_EntityScaleZ#(Game\Stage\Properties\Water))
										If (WaterScaleZ$ <> "") Then ScaleEntity(Game\Stage\Properties\Water, BB_EntityScaleX#(Game\Stage\Properties\Water), BB_EntityScaleY#(Game\Stage\Properties\Water), Float#(WaterScaleZ$))
										Game\Stage\Properties\WaterLevel = xmlNodeAttributeValueGet(InformationChildNode, "level")
									Else
										Game\Stage\Properties\Water = CreatePlane()
										Game\Stage\Properties\WaterLevel = xmlNodeAttributeValueGet(InformationChildNode, "level")
										TranslateEntity(Game\Stage\Properties\Water, 0, Game\Stage\Properties\WaterLevel, 0)
									End If
									If (WaterTexture$ <> "") Then
										Game\Stage\Properties\WaterTexture = LoadTexture(Path$+WaterTexture$)
										EntityTexture(Game\Stage\Properties\Water, Game\Stage\Properties\WaterTexture)
									End If

									Game\Stage\Properties\WaterDistortion = LoadAnimTexture("Textures/Water_Bump.png", 0, 64, 64, 0, 36)
									Game\Stage\Properties\WaterFX = FxManager_RegisterFxMeshPostProcess(Game\Stage\Properties\Water, Game\Stage\Properties\WaterDistortion, Vector(0, 0, 0), Vector(0, 0, 0), Vector(0, 0, 0), 0.4, 0.3)
									ScaleTexture(Game\Stage\Properties\WaterDistortion, 1, 1)
								EndIf

						End Select 
					Next
		
					
				Case "scene"
					; Retrieve stage's scene specifications
					For j = 1 To xmlNodeChildCount(RootChildNode)
						
						; Retrieve child node
						SceneChildNode = xmlNodeChild(RootChildNode, j)
						
						; Find out wich kind of scene entity it is.
						Select xmlNodeNameGet$(SceneChildNode)
							Case "mesh"								
								; Retrieve mesh properties information
								MeshFilename$	= xmlNodeAttributeValueGet(SceneChildNode, "filename") 
								MeshVisible		= xmlNodeAttributeValueGet(SceneChildNode, "visible")
								MeshCollision	= xmlNodeAttributeValueGet(SceneChildNode, "collision")

								; Update progress bar
								Game_Stage_UpdateProgressBar("Loading scene mesh "+j+": "+MeshFilename$, Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
						
								; Load mesh and set visibility. Depending if the mesh should
								; cast collisions, set alpha to 0 or hide the entity. After this
								; set collision type.
								Mesh = LoadMesh(Path$+MeshFilename$, Game\Stage\Root)
								If (MeshVisible = False) Then
									If (MeshCollision = True) Then
										EntityAlpha(Mesh, 0.0)
									Else
										HideEntity(Mesh)
									End If
								End If
								If (MeshCollision = True) Then
									EntityType(Mesh, COLLISION_WORLD_POLYGON_ALIGN)
									EntityPickMode(Mesh, 2)
								End If

								; Create structure
								Struct.MeshStructure = New MeshStructure
								Struct\Entity = Mesh
								
								; Setup position, rotation and scale.
								ScenePosition = xmlNodeFind("position", SceneChildNode)
								If (ScenePosition<>0) Then PositionEntity(Mesh, Float(xmlNodeAttributeValueGet(ScenePosition, "x")), Float(xmlNodeAttributeValueGet(ScenePosition, "y")), Float(xmlNodeAttributeValueGet(ScenePosition, "z")))
								SceneRotation = xmlNodeFind("rotation", SceneChildNode)
								If (SceneRotation<>0) Then RotateEntity(Mesh, xmlNodeAttributeValueGet(SceneRotation, "pitch"), xmlNodeAttributeValueGet(SceneRotation, "yaw"), xmlNodeAttributeValueGet(SceneRotation, "roll"))
								SceneScale = xmlNodeFind("scale", SceneChildNode)
								If (SceneScale<>0) Then ScaleEntity(Mesh, Float(xmlNodeAttributeValueGet(SceneScale, "x")), Float(xmlNodeAttributeValueGet(SceneScale, "y")), Float(xmlNodeAttributeValueGet(SceneScale, "z")))
								; Attributes
								SceneAttributes = xmlNodeFind("attributes", SceneChildNode)
								If (SceneAttributes <> 0) Then
									SceneAttributesAutofade = xmlNodeFind("autofade", SceneAttributes)
									If (SceneAttributesAutofade <> 0) Then EntityAutoFade(Mesh, Float(xmlNodeAttributeValueGet(SceneAttributesAutoFade, "near")), Float(xmlNodeAttributeValueGet(SceneAttributesAutoFade, "far")))
									SceneAttributesColor = xmlNodeFind("color", SceneAttributes)
									If (SceneAttributesColor <> 0) Then EntityColor(Mesh, Float(xmlNodeAttributeValueGet(SceneAttributesColor, "r")), Float(xmlNodeAttributeValueGet(SceneAttributesColor, "g")), Float(xmlNodeAttributeValueGet(SceneAttributesColor, "b")))
									SceneAttributesOrder = xmlNodeFind("order", SceneAttributes)
									If (SceneAttributesOrder <> 0) Then EntityOrder(Mesh, xmlNodeDataGet(SceneAttributesOrder))
									SceneAttributesEffects = xmlNodeFind("effects", SceneAttributes)
									If (SceneAttributesEffects <> 0) Then EntityFX(Mesh, xmlNodeDataGet(SceneAttributesEffects))
									SceneAttributesBlend = xmlNodeFind("blend", SceneAttributes)
									If (SceneAttributesBlend <> 0) Then EntityBlend(Mesh, xmlNodeDataGet(SceneAttributesBlend))
								End If
							Case "animmesh"

								; Retrieve mesh properties information
								MeshFilename$	= xmlNodeAttributeValueGet(SceneChildNode, "filename") 
								MeshVisible		= xmlNodeAttributeValueGet(SceneChildNode, "visible")
								MeshCollision	= xmlNodeAttributeValueGet(SceneChildNode, "collision")

								; Update progress bar
								Game_Stage_UpdateProgressBar("Loading scene animmesh "+j+": "+MeshFilename$, Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
								
								; Load mesh and set visibility. Depending if the mesh should
								; cast collisions, set alpha to 0 or hide the entity. After this
								; set collision type.
								Mesh = LoadAnimMesh(Path$+MeshFilename$, Game\Stage\Root)
								If (MeshVisible = False) Then
									If (MeshCollision = True) Then
										EntityAlpha(Mesh, 0.0)
									Else
										HideEntity(Mesh)
									End If
								End If
								If (MeshCollision = True) Then
									RecursiveEntityType(Mesh, COLLISION_WORLD_POLYGON_ALIGN)
									RecursiveEntityPickMode(Mesh, 2)
								End If

								; Create structure
								Struct.MeshStructure = New MeshStructure
								Struct\Entity = Mesh
								
								; Setup position, rotation and scale.
								ScenePosition = xmlNodeFind("position", SceneChildNode)
								If (ScenePosition<>0) Then PositionEntity(Mesh, Float(xmlNodeAttributeValueGet(ScenePosition, "x")), Float(xmlNodeAttributeValueGet(ScenePosition, "y")), Float(xmlNodeAttributeValueGet(ScenePosition, "z")))
								SceneRotation = xmlNodeFind("rotation", SceneChildNode)
								If (SceneRotation<>0) Then RotateEntity(Mesh, xmlNodeAttributeValueGet(SceneRotation, "pitch"), xmlNodeAttributeValueGet(SceneRotation, "yaw"), xmlNodeAttributeValueGet(SceneRotation, "roll"))
								SceneScale = xmlNodeFind("scale", SceneChildNode)
								If (SceneScale<>0) Then ScaleEntity(Mesh, Float(xmlNodeAttributeValueGet(SceneScale, "x")), Float(xmlNodeAttributeValueGet(SceneScale, "y")), Float(xmlNodeAttributeValueGet(SceneScale, "z")))
								; Attributes
								SceneAttributes = xmlNodeFind("attributes", SceneChildNode)
								If (SceneAttributes <> 0) Then
									SceneAttributesAutofade = xmlNodeFind("autofade", SceneAttributes)
									If (SceneAttributesAutofade <> 0) Then RecursiveEntityAutoFade(Mesh, Float(xmlNodeAttributeValueGet(SceneAttributesAutoFade, "near")), Float(xmlNodeAttributeValueGet(SceneAttributesAutoFade, "far")))
									SceneAttributesColor = xmlNodeFind("color", SceneAttributes)
									If (SceneAttributesColor <> 0) Then RecursiveEntityColor(Mesh, Float(xmlNodeAttributeValueGet(SceneAttributesColor, "r")), Float(xmlNodeAttributeValueGet(SceneAttributesColor, "g")), Float(xmlNodeAttributeValueGet(SceneAttributesColor, "b")))
									SceneAttributesOrder = xmlNodeFind("order", SceneAttributes)
									If (SceneAttributesOrder <> 0) Then RecursiveEntityOrder(Mesh, xmlNodeDataGet(SceneAttributesOrder))
									SceneAttributesEffects = xmlNodeFind("effects", SceneAttributes)
									If (SceneAttributesEffects <> 0) Then RecursiveEntityFX(Mesh, xmlNodeDataGet(SceneAttributesEffects))
									SceneAttributesBlend = xmlNodeFind("blend", SceneAttributes)
									If (SceneAttributesBlend <> 0) Then RecursiveEntityBlend(Mesh, xmlNodeDataGet(SceneAttributesBlend))
								End If
							Case "light"
								; Update progress bar
								Game_Stage_UpdateProgressBar("Loading scene light "+j, Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
								
								; Retrieve mesh properties information
								Light = 0
								LightType$	= xmlNodeAttributeValueGet(SceneChildNode, "type")
								Select LightType$
									Case "directional"
										Light = CreateLight(1)
									Case "point"
										Light = CreateLight(2)
									Case "spot"
										Light = CreateLight(3)
								End Select
								LightRange(Light, Float(xmlNodeAttributeValueGet(SceneChildNode, "range")))
								
								; Setup position, rotation and scale.
								LightPosition = xmlNodeFind("position", SceneChildNode)
								If (LightPosition<>0) Then PositionEntity(Light, Float(xmlNodeAttributeValueGet(LightPosition, "x")), Float(xmlNodeAttributeValueGet(LightPosition, "y")), Float(xmlNodeAttributeValueGet(LightPosition, "z")))
								LighRotation = xmlNodeFind("rotation", SceneChildNode)
								If (LighRotation<>0) Then RotateEntity(Light, xmlNodeAttributeValueGet(LighRotation, "pitch"), xmlNodeAttributeValueGet(LighRotation, "yaw"), xmlNodeAttributeValueGet(LighRotation, "roll"))
								LightCol = xmlNodeFind("color", SceneChildNode)
								If (LightCol<>0) Then LightColor(Light, xmlNodeAttributeValueGet(LightCol, "r"), xmlNodeAttributeValueGet(LightCol, "g"), xmlNodeAttributeValueGet(LightCol, "b"))
										
							Case "object"
								; Find out wich kind of object is is and act consecuently
								Select xmlNodeAttributeValueGet(SceneChildNode, "type")
									Case "player"
										; Update progress bar
										Game_Stage_UpdateProgressBar("Loading object n."+j+": Player", Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
										
										p.tPlayer = Player_Create(CHARACTER_SONIC)
										Camera_Bind(camera, p)
										
										; Setup position, rotation and scale.
										ScenePosition = xmlNodeFind("position", SceneChildNode)
										If (ScenePosition<>0) Then PositionEntity(p\Objects\Entity, Float(xmlNodeAttributeValueGet(ScenePosition, "x")), Float(xmlNodeAttributeValueGet(ScenePosition, "y")), Float(xmlNodeAttributeValueGet(ScenePosition, "z")))
									
									Case "ring"
										; Update progress bar
										Game_Stage_UpdateProgressBar("Loading object n."+j+": Ring", Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
										
										positionX# = 0
										positionY# = 0
										positionZ# = 0
										
										; Setup position, rotation and scale.
										ScenePosition = xmlNodeFind("position", SceneChildNode)
										If (ScenePosition<>0) Then
											positionX# = Float(xmlNodeAttributeValueGet(ScenePosition, "x"))
											positionY# = Float(xmlNodeAttributeValueGet(ScenePosition, "y"))
											positionZ# = Float(xmlNodeAttributeValueGet(ScenePosition, "z"))
										End If
										
										obj.tObject = Object_Ring_Create(positionX#, positionY#, positionZ#)
										
									Case "spring"
										; Update progress bar
										Game_Stage_UpdateProgressBar("Loading object n."+j+": Spring", Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
										
										positionX# = 0
										positionY# = 0
										positionZ# = 0
										
										rotationX# = 0
										rotationY# = 0
										rotationZ# = 0
										
										; Setup position, rotation and scale.
										ScenePosition = xmlNodeFind("position", SceneChildNode)
										If (ScenePosition<>0) Then
											positionX# = Float(xmlNodeAttributeValueGet(ScenePosition, "x"))
											positionY# = Float(xmlNodeAttributeValueGet(ScenePosition, "y"))
											positionZ# = Float(xmlNodeAttributeValueGet(ScenePosition, "z"))
										End If
										
										SceneRotation = xmlNodeFind("rotation", SceneChildNode)
										If (SceneRotation<>0) Then										
											rotationX# = Float(xmlNodeAttributeValueGet(SceneRotation, "pitch"))
											rotationY# = Float(xmlNodeAttributeValueGet(SceneRotation, "yaw"))
											rotationZ# = Float(xmlNodeAttributeValueGet(SceneRotation, "roll"))
										End If
										
										obj.tObject = Object_Spring_Create(positionX#, positionY#, positionZ#, rotationX#, rotationY#, rotationZ#)
										
									Case "monitor"
										; Update progress bar
										Game_Stage_UpdateProgressBar("Loading object n."+j+": Monitor", Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
										
										positionX# = 0
										positionY# = 0
										positionZ# = 0
										
										; Setup position, rotation and scale.
										ScenePosition = xmlNodeFind("position", SceneChildNode)
										If (ScenePosition<>0) Then
											positionX# = Float(xmlNodeAttributeValueGet(ScenePosition, "x"))
											positionY# = Float(xmlNodeAttributeValueGet(ScenePosition, "y"))
											positionZ# = Float(xmlNodeAttributeValueGet(ScenePosition, "z"))
										End If
										
										
										obj.tObject = Object_Monitor_Create(positionX#, positionY#, positionZ#)
									
									Case "bumper"
										; Update progress bar
										Game_Stage_UpdateProgressBar("Loading object n."+j+": Bumper", Float#(j)/Float#(xmlNodeChildCount(RootChildNode)))
										
										positionX# = 0
										positionY# = 0
										positionZ# = 0
										
										; Setup position, rotation and scale.
										ScenePosition = xmlNodeFind("position", SceneChildNode)
										If (ScenePosition<>0) Then
											positionX# = Float(xmlNodeAttributeValueGet(ScenePosition, "x"))
											positionY# = Float(xmlNodeAttributeValueGet(ScenePosition, "y"))
											positionZ# = Float(xmlNodeAttributeValueGet(ScenePosition, "z"))
										End If
																				
										obj.tObject = Object_Bumper_Create(positionX#, positionY#, positionZ#)
								End Select
						End Select 
					Next
	
			End Select 
		Next
		xmlNodeDelete(RootNode)
	
		; Once finished loading stage information, activate music, setup collisions and
		; then, we're ready to go to next step.
		PlaySound(Game\Stage\Properties\Music)

		; Setup collisions within the environment.
		Collisions(COLLISION_PLAYER, COLLISION_WORLD_POLYGON, 		2, 2)
		Collisions(COLLISION_PLAYER, COLLISION_WORLD_POLYGON_ALIGN, 2, 2)
		Collisions(COLLISION_PLAYER, COLLISION_WORLD_BOX,			3, 2)
		Collisions(COLLISION_CAMERA, COLLISION_WORLD_POLYGON, 		2, 1)
		Collisions(COLLISION_CAMERA, COLLISION_WORLD_POLYGON_ALIGN,	2, 1)
		Collisions(COLLISION_CAMERA, COLLISION_WORLD_BOX,			3, 1)

		DeltaTime_Reset(Game\DeltaTime)
		Game\State = GAME_STATE_STEP
				
		; Create fade-in transition effect
		PostEffect_Create_FadeIn(0.01, 10, 10, 10)
		;PostEffect_Create_MotionBlur(0.84)
	End Function

	; ---------------------------------------------------------------------------------------------------------
	; Game_Stage_Step
	; ---------------------------------------------------------------------------------------------------------
	Function Game_Stage_Step()
		If (Input_Lock = True) Then
		
			If (KeyHit(KEY_F1)) Then
				For p.tPlayer = Each tPlayer
					EntityType(p\Objects\Entity, 0)
					
					PositionEntity p\Objects\Entity, 0, 10, 0
					PositionEntity p\Objects\Mesh, 0, 10, 0						
					p\Motion\Speed\x = 0
					p\Motion\Speed\y = 0
					p\Motion\Speed\z = 0
	
					EntityType(p\Objects\Entity, COLLISION_PLAYER)
				Next
			EndIf
	
			; Update game timer and fps count
			Game\Gameplay\Time = Game\Gameplay\Time+(Game\DeltaTime\TimeCurrentFrame-Game\DeltaTime\TimePreviousFrame)
			Game\Others\Frames = Game\Others\Frames + 1
			If (Game\Others\NextFrame < Game\DeltaTime\TimeCurrentFrame) Then
				Game\Others\FPS   	  = Game\Others\Frames
				Game\Others\Frames	  = 0
				Game\Others\NextFrame = Game\DeltaTime\TimeCurrentFrame+1000
			End If
	
			; Update gravity alignment
			AlignToVector(Game\Stage\Gravity, Game\Stage\GravityAlignment\x#, Game\Stage\GravityAlignment\y#, Game\Stage\GravityAlignment\z#, 2)
					
			; Update objects
			For c.tCamera = Each tCamera : Camera_Update(c, Game\DeltaTime) : Next
			For p.tPlayer = Each tPlayer : Player_Update(p, Game\DeltaTime) : Next
			Objects_Update(Game\DeltaTime)
					
			; Update world
			UpdateWorld(Game\DeltaTime\Delta)
	
			; Update water texture
			If (FxManager_Supported = True) Then
				PositionTexture(Game\Stage\Properties\WaterDistortion, Cos(MilliSecs()*0.002)*4, Sin(MilliSecs()*0.002)*4)
	
				If (Game\Stage\Properties\Water <> 0) Then
					EntityTexture(Game\Stage\Properties\WaterFX\NormalMesh, Game\Stage\Properties\WaterDistortion, (MilliSecs()*0.03) Mod 31, 0)
				End If
			End If
			If (Game\Stage\Properties\WaterTexture <> 0) Then PositionTexture(Game\Stage\Properties\WaterTexture,    0, MilliSecs()*0.007)
		End If
		
		; Render world
		If (KeyHit(KEY_CTRL_LEFT)) Then FxManager_Activated = 1-FxManager_Activated
		For c.tCamera = Each tCamera
			CameraProjMode (c\Entity, 1)
			PositionEntity(Game\Stage\Properties\SkyBox, EntityX(c\Entity), EntityY(c\Entity), EntityZ(c\Entity))
			;PositionEntity(Game\Stage\Properties\Water, Cos(MilliSecs()*0.02)*4, EntityY#(Game\Stage\Properties\Water, True), Sin(MilliSecs()*0.02)*4)

			If (Game\Stage\Properties\Water <> 0) Then
				If EntityY(c\Entity) < Game\Stage\Properties\WaterLevel Then
					CameraFogMode c\Entity, 1
					CameraFogColor c\Entity, 70, 80, 120
					CameraFogRange c\Entity, 0.1, 90
					RotateEntity Game\Stage\Properties\Water, 0, 0, 180
					EntityColor Game\Stage\Properties\Water, 255, 255, 255
					EntityBlend Game\Stage\Properties\Water, 1
				Else
					CameraFogMode c\Entity, 0
					RotateEntity Game\Stage\Properties\Water, 0, 0, 0
					EntityColor Game\Stage\Properties\Water, 12, 73, 135
				EndIf
			End If

			;EntityType (c\Entity, 0)
			FxManager_RenderWorldFast()
			;EntityType (c\Entity, 3)
			CameraProjMode (c\Entity, 0)
			PostEffect_UpdateAll(Game\DeltaTime)
			Interface_Render()
		Next 
		Flip(GAME_WINDOW_VSYNC)
	End Function

	; ---------------------------------------------------------------------------------------------------------
	; Game_Stage_End
	; ---------------------------------------------------------------------------------------------------------
	Function Game_Stage_End()
		
	End Function

	; ---------------------------------------------------------------------------------------------------------
	; Game_Stage_UpdateProgressBar
	; ---------------------------------------------------------------------------------------------------------
	Function Game_Stage_UpdateProgressBar(Message$, t#)
		; Clear screen
		Cls

		; Calculate percentage
		Progression = Int(t#*196.0)

		; Draw message at center
		Color(255, 255, 255)
		Text(GAME_WINDOW_W/2-100, GAME_WINDOW_H/2-40, Message$)
		Rect(GAME_WINDOW_W/2-100, GAME_WINDOW_H/2-25, 200, 50, False)
		Rect(GAME_WINDOW_W/2-98, GAME_WINDOW_H/2-23, Progression, 46, True)

		; Flip
		Flip(GAME_WINDOW_VSYNC)
	End Function