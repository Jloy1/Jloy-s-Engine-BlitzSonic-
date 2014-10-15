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
;   Changelog:  -(Mark)----------------------------------->                                                    ;
;               25/01/2008 - Original Version                                                                  ;
;                                                                                                              ;
;==============================================================================================================;
;                                                                                                              ;
;   TODO:                                                                                                      ;
;                                                                                                              ;
;==============================================================================================================;

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   CONSTANTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	Const OBJTYPE_NULL		= 0
	Const OBJTYPE_RING		= 1
	Const OBJTYPE_SPRING	= 2
	Const OBJTYPE_MONITOR	= 3
	Const OBJTYPE_BUMPER	= 4

	Const OBJECT_VIEWDISTANCE_MIN#	= 320
	Const OBJECT_VIEWDISTANCE_MAX#	= 450
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   STRUCTURES
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	Type tObject
		Field ObjType
		
		Field Position.tVector
		
		Field IValues[16]
		Field FValues#[16]
		Field Mode
		Field State	
		
		Field Entity
	End Type

; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   RINGS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; =========================================================================================================
	; Object_Ring_Create
	; =========================================================================================================
	; Creates a New Ring Object

	; =========================================================================================================
	; Object_Ring_Create
	; =========================================================================================================
	; Creates a New Ring Object

	Function Object_Ring_Create.tObject(x#, y#, z#)
	
		o.tObject = New tObject
		
		o\ObjType = OBJTYPE_RING
		
		o\Position = New tVector	
		o\Position\x# = x#
		o\Position\y# = y#
		o\Position\z# = z#
		
		o\Entity = LoadMesh("Objects\Rings\Ring.b3d")
		If (LinePick(x#, y#, z#, 0, -OMEGA#, 0)<>0) Then
			o\IValues[0] = CreateQuad(3, 1.1, 1.5, 0.5)
			EntityTexture(o\IValues[0], Textures_Shadow)
			PositionEntity(o\IValues[0], PickedX#(), PickedY#(), PickedZ#(), True)

			o\FValues[0] = PickedNX#()
			o\FValues[1] = PickedNY#()
			o\FValues[2] = PickedNZ#()
			
			MoveEntity(o\IValues[0], 0, 0.2, 0)
			ScaleEntity(o\IValues[0], 1-Min#((y#-PickedY#())/120, 1), 1-Min#((y#-PickedY#())/120, 1), 1-Min#((y#-PickedY#())/120, 1))
			EntityAlpha(o\IValues[0], 1-Min#((y#-PickedY#())/120, 1))
		End If
		
		EntityAutoFade(o\Entity, OBJECT_VIEWDISTANCE_MIN#, OBJECT_VIEWDISTANCE_MAX#)
		ScaleEntity o\Entity, 0.6, 0.6, 0.6
		TranslateEntity o\Entity, x#, y#, z#	
		
		Return o
		
	End Function
	
	; =========================================================================================================
	; Object_Ring_Update
	; =========================================================================================================
	; Updates the ring object
	
	Function Object_Ring_Update(o.tObject, p.tPlayer, d.tDeltaTime)
	
		RotateEntity o\Entity, 0, Float#(MilliSecs())*0.2, 0
		If (o\IValues[0] <> 0) Then
			RotateEntity o\IValues[0], 0, Float#(MilliSecs())*0.2, 0
			AlignToVector(o\IValues[0], o\FValues[0], o\FValues[1], o\FValues[2], 2)
		End If
		
		; Player collided with ring
		If (Abs(EntityX(p\Objects\Entity) - o\Position\x) < 4.75) And (Abs(EntityY(p\Objects\Entity) - o\Position\y) < 4.75) And (Abs(EntityZ(p\Objects\Entity) - o\Position\z) < 4.75) Then
		
			; Add to ring counter
			Game\Gameplay\Rings = Game\Gameplay\Rings + 1
			Game\Gameplay\Score = Game\Gameplay\Score + 10						

			; Bling!			
			PlaySound Sound_Ring
		
			; Delete the Ring
			FreeEntity o\Entity
			If (o\IValues[0]) Then FreeEntity(o\IValues[0])
			Delete o\Position
			Delete o
			Return
		EndIf	
		
	End Function
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   SPRINGS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
	
	; =========================================================================================================
	; Object_Spring_Create
	; =========================================================================================================
	; Creates a New Spring Object	
	
	Function Object_Spring_Create.tObject(x#, y#, z#, rx#=0, ry#=0, rz#=0)
	
		o.tObject = New tObject
		
		o\ObjType = OBJTYPE_SPRING
		
		o\Position = New tVector	
		o\Position\x# = x#
		o\Position\y# = y#
		o\Position\z# = z#
		
		o\Entity = LoadMesh("Objects\Springs\Spring.b3d")

		o\IValues[0] = LoadMesh("Objects\Quad.b3d", o\Entity)
		EntityBlend o\IValues[0], 3
		EntityFX o\IValues[0], 1+16
		EntityTexture o\IValues[0], LoadTexture("Textures\Woosh.png")
		EntityAlpha o\IValues[0], 0.0
		TranslateEntity o\IValues[0], 0, 1, 0
		o\FValues[0] = 0

		ScaleEntity o\Entity, 1.5, 1.5, 1.5
		TranslateEntity o\Entity, x#, y#, z#
		TurnEntity o\Entity, rx#, ry#, rz#

		
		Return o
		
	End Function
	

	; =========================================================================================================
	; Object_Spring_Update
	; =========================================================================================================
	; Updates the spring object	
	
	Function Object_Spring_Update(o.tObject, p.tPlayer, d.tDeltaTime)

		If (Abs(EntityX(p\Objects\Entity) - o\Position\x) < 12.5) And (Abs(EntityY(p\Objects\Entity) - o\Position\y) < 12.5) And (Abs(EntityZ(p\Objects\Entity) - o\Position\z) < 12.5) Then
		
			;If Not p\Motion\Ground Then PositionEntity p\Objects\Entity, (EntityX(p\Objects\Entity)*39 + o\Position\x#)/40, (EntityY(p\Objects\Entity)*39 + o\Position\y#)/40, (EntityZ(p\Objects\Entity)*39 + o\Position\z#)/40
		
			If (Abs(EntityX(p\Objects\Entity) - o\Position\x) < 5.5) And (Abs(EntityY(p\Objects\Entity) - o\Position\y) < 5.5) And (Abs(EntityZ(p\Objects\Entity) - o\Position\z) < 5.5) And o\State = 0 Then

				o\State = 1
				o\FValues[0] = 0.0
			
				; Transform the Speed vector to the Player space
				TFormVector 0, 1, 0, o\Entity, p\Objects\Entity
				p\Action = ACTION_COMMON
				p\Motion\Ground = False
				p\Motion\Speed\x = TFormedX()*3
				p\Motion\Speed\y = TFormedY()*3
				p\Motion\Speed\z = TFormedZ()*3
				
				; Transform the position vector to World space
				TFormVector 0, 1, 0, o\Entity, 0					
				PositionEntity p\Objects\Entity, o\Position\x# + TFormedX()*4, o\Position\y# + TFormedY()*4, o\Position\z# + TFormedZ()*4
				
				; Sproing!
				PlaySound Sound_Spring
				
				; Apply motion blur
				PostEffect_Create_MotionBlur(0.85)
			
			EndIf
		
		EndIf
		
		If o\State = 1 Then

			o\FValues[0] = o\FValues[0] + 0.05*d\Delta
			ScaleEntity o\IValues[0], 1+o\FValues[0]*6, 3+o\FValues[0]*6, 1+o\FValues[0]*6
			TurnEntity o\IValues[0], 0, 5*d\Delta, 0
			EntityAlpha o\IValues[0], (1-o\FValues[0])*0.7
			
			ScaleEntity o\Entity, 1.5, 1.5 + Sin (o\FValues[0]*180)*1.5, 1.5
			
			If o\FValues[0] >= 1.0 Then
				ScaleEntity o\IValues[0], 1, 1, 1
				o\FValues[0] = 0
				o\State = 0
				ScaleEntity o\Entity, 1.5, 1.5, 1.5
			EndIf
								
		EndIf
		
		
	End Function
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   MONITORS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
	
	; =========================================================================================================
	; Object_Monitor_Create
	; =========================================================================================================
	; Creates a New Monitor Object	
	
	Function Object_Monitor_Create.tObject(x#, y#, z#)
	
		o.tObject = New tObject
		
		o\ObjType = OBJTYPE_MONITOR
		
		o\Position = New tVector	
		o\Position\x# = x#
		o\Position\y# = y#
		o\Position\z# = z#
		
		o\Entity = LoadMesh("Objects\Monitor\Monitor.b3d")
		;EntityType o\Entity, 2
		ScaleEntity o\Entity, 1.1, 1.1, 1.1
		TranslateEntity o\Entity, x#, y#, z#	
		
		Return o
		
	End Function
	

	; =========================================================================================================
	; Object_Monitor_Update
	; =========================================================================================================
	; Updates the Monitor object	
	
	Function Object_Monitor_Update(o.tObject, p.tPlayer, d.tDeltaTime)
	
		; Player collided with monitor
		If (Abs(EntityX(p\Objects\Entity) - o\Position\x) < 4.5) And (Abs(EntityY(p\Objects\Entity) - o\Position\y) < 6.5) And (Abs(EntityZ(p\Objects\Entity) - o\Position\z) < 4.5) And p\Action = ACTION_JUMP Then
		
			; Add to ring counter
			Game\Gameplay\Rings = Game\Gameplay\Rings + 10
			Game\Gameplay\Score = Game\Gameplay\Score + 10			
			
			If p\Motion\Speed\y# < 0 Then 
				p\Motion\Speed\y# = -p\Motion\Speed\y#
			EndIf

			; Play the explosion sound
			PlaySound Sound_Explosion
		
			; Delete the Monitor
			FreeEntity o\Entity 
			Delete o\Position
			Delete o
			Return
			
		EndIf	
		
	End Function	
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   BUMPERS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

	; =========================================================================================================
	; Object_Bumper_Create
	; =========================================================================================================
	; Creates a New Bumper Object

	Function Object_Bumper_Create.tObject(x#, y#, z#)
	
		o.tObject = New tObject
		
		o\ObjType = OBJTYPE_BUMPER
		
		o\Position = New tVector	
		o\Position\x# = x#
		o\Position\y# = y#
		o\Position\z# = z#
		
		o\Entity = LoadMesh("Objects\Bumper\Bumper.b3d")
		ScaleEntity o\Entity, 0.2, 0.2, 0.2
		TranslateEntity o\Entity, x#, y#, z#	
		
		Return o
		
	End Function
	
	; =========================================================================================================
	; Object_Bumper_Update
	; =========================================================================================================
	; Updates the Bumperobject
	
	Function Object_Bumper_Update(o.tObject, p.tPlayer, d.tDeltaTime)
	
		TurnEntity o\Entity, 0, 1.2*d\Delta, 0
		
		; Player collided with bumper
		If (Abs(EntityX(p\Objects\Entity) - o\Position\x) < 3.7) And (Abs(EntityY(p\Objects\Entity) - o\Position\y) < 3.7) And (Abs(EntityZ(p\Objects\Entity) - o\Position\z) < 3.7) And ((o\State = 0) Or (o\FValues[0] > 100)) Then
		
			o\State = 1
			o\FValues[0] = 40.0
		
			Game\Gameplay\Score = Game\Gameplay\Score + 10						

			; Bump!
			PlaySound Sound_Bumper
			
			x# = o\Position\x# - EntityX(p\Objects\Entity)
			y# = o\Position\y# - EntityY(p\Objects\Entity)
			z# = o\Position\z# - EntityZ(p\Objects\Entity)						
			
			l# = Sqr(x^2 + y^2 + z^2)*1.5
						
			TFormVector x/l, y/l, z/l, 0, p\Objects\Entity
			p\Action = ACTION_COMMON
			p\Motion\Ground = False
			p\Motion\Speed\x = p\Motion\Speed\x-TFormedX()*3
			p\Motion\Speed\y = p\Motion\Speed\y-TFormedY()*3
			p\Motion\Speed\z = p\Motion\Speed\z-TFormedZ()*3
			
			; Apply motion blur
			PostEffect_Create_MotionBlur(0.85)			
		
		EndIf
		
		If o\State = 1 Then
			o\FValues[0] = o\FValues[0] + 18*d\Delta

			; Wooble effect
			ScaleEntity o\Entity, 0.2-0.2*Cos(o\FValues[0])/(o\FValues[0]*0.01), 0.2-0.2*Sin(o\FValues[0])/(o\FValues[0]*0.01), 0.2-0.2*Sin(o\FValues[0])/(o\FValues[0]*0.01)
			;ScaleEntity o\Entity, 0.2-0.1*Cos(o\FValues[0])/(o\FValues[0]*0.01), 0.2-0.1*Cos(o\FValues[0])/(o\FValues[0]*0.01), 0.2-0.1*Cos(o\FValues[0])/(o\FValues[0]*0.01)


			If o\FValues[0] >= 4500 Then
				o\State = 0
				o\FValues[0] = 0.0
			EndIf

		EndIf
		
	End Function	
	
	
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
;   ALL OBJECTS
; /\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
	
	Function Objects_Update(d.tDeltaTime)
	
		For p.tPlayer = Each tPlayer
			
			For o.tObject = Each tObject
				If (EntityDistance(p\Objects\Entity, o\Entity) < OBJECT_VIEWDISTANCE_MAX#) Then
					ShowEntity(o\Entity)
					Select o\ObjType
				
						Case OBJTYPE_NULL
						
						Case OBJTYPE_RING
							Object_Ring_Update(o, p, d)
							
						Case OBJTYPE_SPRING
							Object_Spring_Update(o, p, d)					
	
						Case OBJTYPE_MONITOR
							Object_Monitor_Update(o, p, d)								
				
						Case OBJTYPE_BUMPER
							Object_Bumper_Update(o, p, d)		
						
					End Select
				Else
					HideEntity(o\Entity)
				End If
			Next
		Next	
	
	End Function
;~IDEal Editor Parameters:
;~C#Blitz3D