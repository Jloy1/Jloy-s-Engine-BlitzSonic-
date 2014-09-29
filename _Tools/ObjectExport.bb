Graphics3D(640, 480, 0, 2)

Mesh = LoadAnimMesh(CommandLine$())
If (Mesh = 0) Then RuntimeError("Couldn't load mesh.")

File = WriteFile("objects.xml")
RecursiveExport(File, Mesh)
CloseFile(File)
End

Function RecursiveExport(File, Mesh)

	ObjType$ = ""
	If (Instr(Lower$(EntityName$(Mesh)), "spring")<>0) Then : 		ObjType$ = "spring"
	Else If (Instr(Lower$(EntityName$(Mesh)), "ring")<>0) Then :	ObjType$ = "ring"
	Else If (Instr(Lower$(EntityName$(Mesh)), "monitor")<>0) Then :	ObjType$ = "monitor"
	Else If (Instr(Lower$(EntityName$(Mesh)), "bumper")<>0) Then :	ObjType$ = "bumper"
	End If

	If (ObjType$ <> "") Then
		WriteLine(File, "		<object type="+Chr$(34)+ObjType+Chr$(34)+">")
		WriteLine(File, "			<position x="+Chr$(34)+EntityX#(Mesh)+Chr$(34)+" y="+Chr$(34)+EntityY#(Mesh)+Chr$(34)+" z="+Chr$(34)+EntityZ#(Mesh)+Chr$(34)+"/>")
		If (ObjType$ = "spring") Then
			WriteLine(File, "			<rotation pitch="+Chr$(34)+EntityPitch#(Mesh)+Chr$(34)+" yaw="+Chr$(34)+EntityYaw#(Mesh)+Chr$(34)+" roll="+Chr$(34)+EntityRoll#(Mesh)+Chr$(34)+"/>")
		End If
		WriteLine(File, "		</object>")
	End If

	For i=1 To CountChildren(Mesh)
		RecursiveExport(File, GetChild(Mesh, i))
	Next

End Function