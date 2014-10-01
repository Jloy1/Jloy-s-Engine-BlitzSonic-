textString$ = "ABC:XYZ"
For i = 1 To Len(textString$)
	Print Asc(Mid$(textString$, i, 1))
Next
WaitKey
;~IDEal Editor Parameters:
;~C#Blitz3D