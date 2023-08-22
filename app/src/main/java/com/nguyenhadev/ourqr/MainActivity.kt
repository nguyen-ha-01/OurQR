package com.nguyenhadev.ourqr

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nguyenhadev.ourqr.ui.theme.OurQRTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Snackbar
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nguyenhadev.ourqr.qr.Analyzer
import com.nguyenhadev.ourqr.ui.theme.Size.*
import com.google.zxing.BinaryBitmap
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.qrcode.QRCodeReader
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.core.graphics.drawable.toBitmap
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nguyenhadev.ourqr.db.PATH
import com.nguyenhadev.ourqr.db.QRScanned
import com.nguyenhadev.ourqr.qr.CreateWork
import com.nguyenhadev.ourqr.qr.QRDataType.*
import com.nguyenhadev.ourqr.qr.QrWriter
import com.nguyenhadev.ourqr.qr.getSizeOfFrame
import com.nguyenhadev.ourqr.qr.point.PointFrame
import com.nguyenhadev.ourqr.qr.point.SolidPoint
import com.google.zxing.common.GlobalHistogramBinarizer
import com.nguyenhadev.ourqr.vm.MainViewModel
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        pm()
        setContent {
            OurQRTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val controller = rememberNavController()
                    LoginHost(controller,Modifier)
                }
            }
        }
    }
    fun pm(pms:List<String> = listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
        requestPermissions(pms.toTypedArray(),1)
    }
}

@Composable
fun HomeScreen(modifier: Modifier ,controller: NavHostController, viewModel: MainViewModel){
    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween, Alignment.CenterVertically){
            //main action buttons
            ButtonToNewScreen(
                Modifier
                    .weight(1f)
                    .background(colorScheme.background), doOnClick = {controller.navigate(Route.Scan.route)}, res = Route.Scan.resId)
            ButtonToNewScreen(
                Modifier
                    .weight(1f)
                    .background(colorScheme.background), doOnClick = {controller.navigate(Route.CreateQr.route)} , res = Route.CreateQr.resId)

        }
        //scan history
        ScannedHistory(Modifier,controller,viewModel)
    }

}

@Composable
fun ScannedHistory(modifier: Modifier = Modifier, controller: NavHostController, viewModel: MainViewModel) {
    //title
    val ctx = LocalContext.current
    val rotate = animateFloatAsState(targetValue = if(viewModel.scannedQrListState) 0f else -180f)
    Surface(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(8.dp), color = colorScheme.primary ,
        shape = RoundedCornerShape(8.dp)
    ){
        Column(Modifier) {
            Row(Modifier.padding(8.dp)){
                Text(text = "History",modifier = Modifier, color = colorScheme.onPrimary, fontSize = typography.labelLarge.fontSize)
                Spacer(modifier = Modifier.weight(1f))
                Icon(painter = painterResource(R.drawable.down_arrow), contentDescription = "expan option",
                    Modifier
                        .size(Medium.size)
                        .graphicsLayer(rotationZ = rotate.value)
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource(),
                            onClick = {
                                viewModel.scannedQrListState = !viewModel.scannedQrListState

                            }) )
            }
            ListScannedQr(controller,viewModel)
        }
    }
}

@Composable
fun ListScannedQr(controller: NavHostController, viewModel: MainViewModel) {
    val ctx = LocalContext.current
//    viewModel.getUpdateScannedQrList(ctx)
    viewModel.getPaging(ctx)
    var paging = remember{viewModel.getPaging(ctx)}
    var items = paging.flow.collectAsLazyPagingItems()
    LazyColumn(Modifier.background(colorScheme.secondary)){
        if (viewModel.scannedQrListState){

            items(items.itemCount){key->
                var item = items.get(key)
                item?.let {
                    itemScannedQR(it)
                }


            }

        }
    }

}
@Composable
fun itemScannedQR(qrScanned: QRScanned) {
    val clipboard = LocalClipboardManager.current

    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(shape = RoundedCornerShape(ExtremeSmall.size), color = colorScheme.primary)

            ){
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = qrScanned.date, Modifier.padding( ExtremeSmall.size), color = colorScheme.onSecondary)
            }
            Row (verticalAlignment = Alignment.CenterVertically){
                val w = with(LocalDensity.current){
                    LocalView.current.width.toDp()*0.5f
                }
                val ctx = LocalContext.current
                Text(text = qrScanned.content,
                    Modifier
                        .padding(horizontal = ExtremeSmall.size)
                        .widthIn(max = w), color = colorScheme.onSecondary, maxLines = 1)
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { /*TODO  copy content to clipboard*/
                    val content = AnnotatedString(text = qrScanned.content,  paragraphStyle = ParagraphStyle())

                    clipboard.setText(content)

                    Toast.makeText(ctx, content.text + " is copied.", Toast.LENGTH_SHORT).show()

                }, Modifier.padding(end = 8.dp, bottom = 8.dp)) {
                    Icon(painter = painterResource(id = R.drawable.copyicon), contentDescription = "copy",tint = colorScheme.onPrimary)
                }
            }

        }
    }
}

@Composable
fun ButtonToNewScreen(modifier: Modifier = Modifier , doOnClick : ()->Unit = {}, res: Int){
    Box(
        modifier = modifier
            .padding(8.dp)
            .clickable { doOnClick() }
            .background(colorScheme.primary, RoundedCornerShape(8.dp))
    ) {
       Column(
           Modifier
               .fillMaxWidth()
               .clickable { doOnClick() }, horizontalAlignment = Alignment.CenterHorizontally) {
           Spacer(modifier = Modifier.height(ExtremeSmall.size))
           Icon(painter = painterResource(id = res), contentDescription = "", Modifier.size(ExtremeLarge.size), tint = colorScheme.onPrimary)
           Spacer(modifier = Modifier.height(ExtremeSmall.size))
       }
    }
}
@Composable
fun ScanScreen(controller: NavHostController ,viewModel: MainViewModel){
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(colorScheme.background)) {
    Camera(controller, viewModel)
    }
}
@Composable
fun MyQrsScreen(controller: NavHostController, viewModel: MainViewModel){
    Column(Modifier.fillMaxSize()) {
        LazyColumn(){
            viewModel.listFolder.forEach {
                item {
                    folderItem(folder = it, controller = controller)
                }
            }
        }
    }
}
@Composable
fun folderItem(folder: Folder, controller: NavHostController) {
    Card(
        Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
            .background(colorScheme.background)
            .clickable {
                // TODO: navigate to folder screen that will show QRs in the folder
            },
    ) {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            Text(text = folder.name, Modifier.background(MaterialTheme.colorScheme.primary))
        }
    }
}
@Composable
fun BottomBar(controller: NavHostController) {
    var onSelected by remember {
        mutableStateOf(0)
    }
    BottomAppBar(
        Modifier
            .fillMaxWidth()
            .background(colorScheme.primary)
            .padding(0.dp),colorScheme.primary) {
        Card(colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)) {
            Row() {
               NavigationBarItem(selected = onSelected==0, onClick = {
                    onSelected=0
                    controller.navigate(BottomNavItem.Home.route) },
                   modifier = Modifier.padding(8.dp),
                   colors = NavigationBarItemDefaults.colors(MaterialTheme.colorScheme.onPrimary,  indicatorColor = MaterialTheme.colorScheme.secondary),
                   icon = {Icon(painterResource(id = BottomNavItem.Home.res),"scan icon",Modifier.size(Large.size))})

            NavigationBarItem(selected = onSelected==1, onClick = {
                onSelected=1
                controller.navigate(BottomNavItem.MyQr.route) },
                modifier = Modifier.padding(8.dp),
                colors = NavigationBarItemDefaults.colors(MaterialTheme.colorScheme.onPrimary,  indicatorColor = MaterialTheme.colorScheme.secondary),
                icon = {Icon(painterResource(id = BottomNavItem.MyQr.res),"scan icon",Modifier.size(Large.size))})
        }
        }
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun Camera(controller: NavHostController,viewModel: MainViewModel){

    val ctx =  LocalContext.current
    viewModel.cameraPermission =ctx.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    viewModel.launcher  = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {
        if (!it){Toast.makeText(ctx, "camera was denied",  Toast.LENGTH_SHORT).show()
            viewModel.cameraPermission = true}
        else viewModel.cameraPermission = false
    })
    if(ctx.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        viewModel.cameraPermission = false
    SideEffect() {
        if (!viewModel.cameraPermission)
        viewModel.launcher!!.launch(Manifest.permission.CAMERA)
    }
    if (viewModel.cameraPermission){
        CameraView(controller , viewModel)
    }
}

@Composable
fun  CameraView(controller: NavHostController,viewModel: MainViewModel) {
    val ctx = LocalContext.current
    val camera = remember {
        ProcessCameraProvider.getInstance(ctx)
    }
    val lifecycle = LocalLifecycleOwner.current

    val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()
    val mainEx = ContextCompat.getMainExecutor(ctx)
    val preview = androidx.camera.core.Preview.Builder().apply {

    }.build()
    val imageAnalyzer = ImageAnalysis.Builder().build().also {
        it.setAnalyzer(mainEx, Analyzer{ w,h,red,green,blue->
            val redSrc = PlanarYUVLuminanceSource(red, w,h, 0,0,w,h,false)
            val greenSrc = PlanarYUVLuminanceSource(green, w,h, 0,0,w,h,false)
            val blueSrc = PlanarYUVLuminanceSource(blue, w,h, 0,0,w,h,false)
            val map = BinaryBitmap(GlobalHistogramBinarizer(greenSrc))
            try{
                val result = QRCodeReader().decode(map)
                Log.d("Scan", result.text)
                viewModel.snackBarData = result.text
                viewModel.scanData = result.text
                viewModel.snackBarHostState = true
            }catch (e:Exception){
                viewModel.snackBarData = viewModel.getCurrentTime()
                viewModel.snackBarHostState = true
                Log.d("time",  e.message.toString())
            }
        })
    }

    DisposableEffect(key1 = ctx, effect = {
        onDispose {
            camera.get().unbindAll()
            camera.cancel(true)
            if(viewModel.scanData.length > 1){
                viewModel.saveScannerQR(ctx, viewModel.scanData)
                viewModel.scanData = ""

            }
            viewModel.snackBarHostState = false
        }
    })
    val useCaseGroup = UseCaseGroup.Builder().addUseCase(preview).addUseCase(imageAnalyzer).build()
    Box(contentAlignment = Alignment.TopCenter){
        AndroidView(factory ={ctx->
            val previewView = PreviewView(ctx)

            preview.setSurfaceProvider(previewView.surfaceProvider)
            camera.addListener(kotlinx.coroutines.Runnable {
                camera.get().bindToLifecycle(lifecycle, cameraSelector,  useCaseGroup)
            },  mainEx)
            previewView
        } , Modifier.fillMaxSize())
        // TODO:  now we create animation for scanner
        val isAnimationOn by remember {
            mutableStateOf(true)
        }
        val height  = LocalView.current.height
        val width = LocalView.current.width
        val dpHeight = with(LocalDensity.current){
            height.toDp()
        }
        val dpWidth = with(LocalDensity.current){
            width.toDp()
        }
        val infinity = rememberInfiniteTransition()
        var animatedHeight = infinity.animateFloat(initialValue = 0f, targetValue =height/3f, animationSpec = infiniteRepeatable(tween(durationMillis = 4000)))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(dpHeight/4))
            Canvas(modifier = Modifier
                .width(dpWidth / 2)
                .height(dpHeight / 3)
                .alpha(0.2f)
                .background(colorScheme.secondary)
                .alpha(1f)
                .border(4.dp, colorScheme.primary)){

                val brush = Brush.verticalGradient(tileMode = TileMode.Decal, colors = listOf(Color(255, 255, 255, 255),Color(249, 201, 201, 255),Color(255, 0, 0, 255)))
                drawRect(brush, topLeft = Offset.Zero, size = Size(this.size.width,  animatedHeight.value))
            }

            }
    }

}
@Composable
fun CreateQr(navController: NavHostController,viewModel: MainViewModel){
    Surface {

        val listType = listOf(Email, Wifi,Link, Text)
        var selected by remember{
            mutableStateOf(0)
        }

        Column (verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = selected ) {
                    listType.forEachIndexed { index, it->
                        val isSelected = index == selected
                        Tab(selected = isSelected, onClick = { selected = index;  viewModel.createType = it },
                            Modifier
                                .padding(4.dp)
                                .background(
                                    if (isSelected) colorScheme.primary else colorScheme.background,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(vertical = ExtremeSmall.size)) {
                            Text(it.name, color = if(isSelected) colorScheme.onPrimary else colorScheme.onBackground,  fontSize = typography.labelLarge.fontSize)
                        }
                    }
                    
                }
            }
            tabContentType(viewModel)
        }
    }
}
@Composable
fun tabContentType(vm: MainViewModel){

    val ctx = LocalContext.current
    val wm = WorkManager.getInstance(ctx)

    var text by remember{ mutableStateOf("_") }
    var scroolState = rememberScrollState()

    Column(Modifier.scrollable(scroolState,  orientation = Orientation.Vertical)) {
        BasicTextField(value = text, onValueChange = {value-> text=value}, maxLines = 2,modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                colorScheme.secondary
            ))
        Button(onClick = { val path  = File(ctx.filesDir.path, PATH )
            if (path.exists() != true) {path.mkdirs(); Toast.makeText(ctx, ctx.filesDir.path, Toast.LENGTH_SHORT)
                .show()
            }
            else{
                Toast.makeText(ctx, path.absolutePath, Toast.LENGTH_LONG).show()
            }
            val writer = QrWriter()
            val bitmapBackground  = ctx.getDrawable(R.drawable.logo)!!.toBitmap(800,800,Bitmap.Config.ARGB_8888)
            val bitmap = writer.write(text,bitmapBackground )
            Toast.makeText(ctx, bitmap.byteCount.toString(), Toast.LENGTH_SHORT).show()
            val file  = File(path, text+".jpeg")

            if(!file.exists()) {
                file.createNewFile()
                Toast.makeText(ctx, "create", Toast.LENGTH_SHORT).show()
                val stream =       FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG,  100,  stream)
                stream.close()
            }else{
                Toast.makeText(ctx, "can't make file", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("run")
        }
        val bitmap  = ctx.getDrawable(R.drawable.logo)?.toBitmap(800,800,Bitmap.Config.ARGB_8888)
        Image(bitmap = bitmap!!.asImageBitmap(), contentDescription ="" )
    }
}
fun runWorker(workManager: WorkManager,string: String, fileName: String){
    val data = Data.Builder()
    data.putString(CreateWork.InputString, string)
    data.putString(CreateWork.Name, fileName)
    val work  = OneTimeWorkRequestBuilder<CreateWork>().setInputData(data.build()).build()
    workManager.enqueue(work)
}