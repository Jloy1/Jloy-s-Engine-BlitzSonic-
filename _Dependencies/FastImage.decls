.lib "FastImage.dll"

InitDraw% (Direct3DDevice7%) : "InitDraw_"
DeinitDraw%() : "DeinitDraw_"
StartDraw% () : "StartDraw_"
EndDraw% () : "EndDraw_"
SetCustomState% (operation%, value%) : "SetCustomState_"
SetCustomTextureState% (operation%, value%) : "SetCustomTextureState_"
SetMipLevel% (level%) : "SetMipLevel_"
SetBlend% (blend%) : "SetBlend_"
SetAlpha% (alpha#) : "SetAlpha_"
SetColor% (r%, g%, b%) : "SetColor_"
SetCustomColor% (colorVertex0%, colorVertex1%, colorVertex2%, colorVertex3%) : "SetCustomColor_"
SetRotation% (rotation#) : "SetRotation_"
SetScale% (scaleX#, scaleY#) : "SetScale_"
SetTransform% (rotation#, scaleX#, scaleY#) : "SetTransform_"
SetMatrix% (xx#, xy#, yx#, yy#) : "SetMatrix_"
SetHandle% (x%, y%) : "SetHandle_"
SetOrigin% (x%, y%) : "SetOrigin_"
MidHandleImage% (image%) : "MidHandleImage_"
SetImageHandle% (image%, x%, y%) : "SetImageHandle_"
AutoMidHandleEx% (state%) : "AutoMidHandleEx_"
AutoImageFlags% (flags%) : "AutoImageFlags_"
SetLineWidth% (width#) : "SetLineWidth_"
SetViewport% (x%, y%, width%, height%) : "SetViewport_"
CreateImageEx_% (texure%, width%, height%, flags%) : "CreateImageEx_"
FreeImageEx% (image%) : "FreeImageEx_"
DrawImageEx_% (image%, x%, y%, frame%) : "DrawImageEx_"
DrawImageRectEx_% (image%, x%, y%, width%, height%, frame%) : "DrawImageRectEx_"
DrawImagePart_% (image%, x%, y%, width%, height%, partX%, partY%, partWidth%, partHeight%, frame%, wrap%) : "DrawImagePart_"
DrawPoly_% (x%, y%, bank%, image%, frame%, color%) : "DrawPoly_"
DrawRect_% (x%, y%, width%, height%, fill%) : "DrawRect_"
DrawRectSimple_% (x%, y%, width%, height%, fill%) : "DrawRectSimple_"
DrawLine% (x%, y%, x2%, y2%) : "DrawLine_"
DrawLineSimple% (x%, y%, x2%, y2%) : "DrawLineSimple_"
DrawPlot% (x%, y%) : "DrawPlot_"
DrawOval% (x%, y%, width%, height%) : "DrawOval_"
GetProperty%(type*) : "GetProperty_"
GetImageProperty%(image%, type*) : "GetImageProperty_"
SetProjScale% (scaleX#, scaleY#) : "SetProjScale_"
SetProjRotation% (rotation#) : "SetProjRotation_"
SetProjTransform% (rotation#, scaleX#, scaleY#) : "SetProjTransform_"
SetProjOrigin% (x%, y%) : "SetProjOrigin_"
SetProjHandle% (x%, y%) : "SetProjHandle_"
MidHandleProj% () : "MidHandleProj_"

CreateImageFont% (Image%, type*) : "CreateImageFont_"
SetImageFont% (font%) : "SetImageFont_"
FreeImageFont% (font%) : "FreeImageFont_"
DrawText_% (text$, x%, y%, centerX%, centerY%) : "DrawText_"
StringWidthEx%(text$) : "StringWidthEx_"
StringHeightEx%(text$) : "StringHeightEx_"
GetFontProperty%(font%, type*) : "GetFontProperty_"

.lib " "

CreateImageEx% (texure%, width%, height%, flags%)
DrawImageEx% (image%, x%, y%, frame%)
DrawImageRectEx% (image%, x%, y%, width%, height%, frame%)
DrawImagePart% (image%, x%, y%, width%, height%, tx%, ty%, twidth%, theight%, frame%)
DrawRect% (x%, y%, width%, height%, fill%)
DrawRectSimple% (x%, y%, width%, height%, fill%)
SetCustomBlend% (src%, dest%)

LoadImageFont% (filename$, flags%)
DrawText% (txt$, x%, y%, centerX%, centerY%)