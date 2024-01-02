package com.example.pokemon.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.pokemon.R
import com.example.pokemon.data.models.PokemonListEntry
import com.example.pokemon.viewmodel.PokemonListViewModel

const val TAG = "MainScreen"

@Composable
fun PokemonListScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Spacer(modifier = Modifier.padding(vertical = 16.dp))
        Image(
            painter = painterResource(id = R.drawable.pokemon_logo),
            contentDescription = "logo",
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            hint = "Search"
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        PokemonList(navController = navController)

    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }


    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(20))
                .background(Color.White, shape = RoundedCornerShape(20))
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused
                }
        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }

}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }

    //  LazyColumn(contentPadding = PaddingValues(16.dp)) {
//        val itemCount = if (pokemonList.size % 2 == 0) {
//            pokemonList.size / 2
//        } else {
//            pokemonList.size / 2 + 1
//        }
    //  val itemCount = pokemonList.size


//        items(itemCount) {
//            if (it >= itemCount - 1 && !endReached) {
//                Log.d(TAG, "end Reached")
//                viewModel.loadPokemonPaginated()
//            }
    //PokemonRow(rowIndex = it, details = pokemonList, navController = navController)
    PokemonCardTest(details = pokemonList, navController = navController)
    //    }
    //  }
}

@Composable
fun PokemonCardView(
    detail: PokemonListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(10.dp)
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate("pokemon_detail_screen/${dominantColor.toArgb()}/${detail.pokemonName}")
            },

        ) {

        Column {

            AsyncImage(
                model = detail.imageUrl,
                contentDescription = detail.pokemonName,
                placeholder = painterResource(
                    id = R.drawable.pokemon_logo
                ),
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)
            )
//            CoilImage(
//                request = ImageRequest.Builder(LocalContext.current)
//                    .data(detail.imageUrl)
//                    .target {
//                        viewModel.calculateDominantColor(it) { color ->
//                            dominantColor = color
//                        }
//                    }
//                    .build(),
//                contentDescription = detail.pokemonName,
//                fadeIn = true,
//                modifier = Modifier
//                    .size(120.dp)
//                    .align(CenterHorizontally)
//            ) {
//                CircularProgressIndicator(
//                    color = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.scale(0.5f)
//                )
//            }
            Text(
                text = detail.pokemonName,
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun PokemonRow(
    rowIndex: Int,
    details: List<PokemonListEntry>,
    navController: NavController
) {
    Column {
        Row {
            PokemonCardView(
                detail = details[rowIndex * 2], navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (details.size >= rowIndex * 2 + 2) {
                PokemonCardView(
                    detail = details[rowIndex * 2], navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun PokemonCardTest(
    details: List<PokemonListEntry>,
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    val dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    val itemCount = pokemonList.size
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(20.dp),
        content = {
            items(details.size) {
                if (it >= itemCount - 1 && !endReached) {
                    viewModel.loadPokemonPaginated()
                }
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .shadow(5.dp, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .aspectRatio(1f)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    dominantColor,
                                    defaultDominantColor
                                )
                            )
                        )
                        .clickable {
                            navController.navigate("pokemon_detail_screen/${dominantColor.toArgb()}/${details[it].pokemonName}")
                        },
                    contentAlignment = Center
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubcomposeAsyncImage(
                            model = details[it].imageUrl,
                            contentDescription = details[it].pokemonName,
                            modifier = Modifier
                                .size(120.dp)
                                .align(CenterHorizontally)
                        ) {
                            val state = painter.state
                            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.scale(0.5f)
                                )
                            } else {
                                SubcomposeAsyncImageContent()
                            }
                        }
//                        AsyncImage(
//                            model = details[it].imageUrl,
//                            contentDescription = details[it].pokemonName,
//                            placeholder = painterResource(
//                                id = R.drawable.pokemon_logo
//                            ),
//                            modifier = Modifier
//                                .size(120.dp)
//                                .align(CenterHorizontally),
//                            contentScale = ContentScale.FillBounds,
//                            onLoading = { loading ->
//
////                                if (loading.equals(true)){
////                                    CircularProgressIndicator(
////                                        color = MaterialTheme.colorScheme.primary,
////                                        modifier = Modifier.scale(0.5f)
////                                    )
////                                }
//
//                            },
//                            onSuccess = {
//                                Log.d(TAG, "success $it")
//                            }
//                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = details[it].pokemonName,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        })
}

