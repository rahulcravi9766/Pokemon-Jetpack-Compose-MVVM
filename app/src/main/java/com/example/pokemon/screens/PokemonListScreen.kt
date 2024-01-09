package com.example.pokemon.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.flatMap
import androidx.paging.map
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.pokemon.R
import com.example.pokemon.data.models.PokemonListToCache
import com.example.pokemon.viewmodel.PokemonListViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val TAG = "MainScreen"

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val isLoading by remember { viewModel.isLoading }
    val searchedKey = remember {
        mutableStateOf("")
    }



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
//                .clickable(onClick = {
//                    scope.launch {
//                        //val data =   viewModel.getAllPokemon()
//                        //Log.d("getAllPokemon", "${viewModel.getAllPokemon()}")
//                    }
//                })
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            hint = "Search"
        ) {
            scope.launch {
//                if (it.length >= 2) {
                    searchedKey.value = it
                     viewModel.getSearchedPokemon(it)
//                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        PokemonList(
            navController = navController,
            isLoading = isLoading,
            searchedKey = searchedKey
        )
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
    viewModel: PokemonListViewModel = hiltViewModel(),
    isLoading: Boolean,
    searchedKey: MutableState<String>
) {
    val pokemonList by remember { viewModel.pokemonList }
    val loadError by remember { viewModel.loadError }
    Log.d("PokemonList", " called")
    PokemonCardView(isLoading = isLoading, navController = navController, searchedKey = searchedKey)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.scale(0.5f)
            )
        }
        if (loadError.isNotEmpty()) {
            RetryButton(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }
}


@Composable
fun PokemonCardView(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel(),
    isLoading: Boolean,
    searchedKey: MutableState<String>
) {
    val endReached by remember { viewModel.endReached }
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    val dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    val pokemonList = remember {
        viewModel.getPagedPokemon()
    }

    val searchedPokemonList = remember {
        viewModel.searchedPokemonList
    }
    val pagedSearchedPokemonList = searchedPokemonList.value.collectAsLazyPagingItems()
    val pagedPokemonList = pokemonList.collectAsLazyPagingItems()


//    Log.d(TAG, "searchedKey ${searchedKey.value}" )
//    pagedSearchedPokemonList.itemSnapshotList.items.mapIndexed { index, searchedListToCache ->
//       Log.d(TAG, "index $index searchedList $searchedListToCache" )
//   }
    val newList = if (pagedSearchedPokemonList.itemSnapshotList.items.isNotEmpty() && searchedKey.value.isNotEmpty()){
        pagedSearchedPokemonList
    }else{
        pagedPokemonList
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(20.dp),
        content = {
            if (newList.itemSnapshotList.isEmpty()){
                item {
                    Text(text = "No Pokemon found", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
                }
            }
            items(newList.itemCount) {
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
                            navController.navigate("pokemon_detail_screen/${dominantColor.toArgb()}/${newList[it]?.pokemonName}")
                        },
                    contentAlignment = Center
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubcomposeAsyncImage(
                            model = newList[it]?.imageUrl,
                            contentDescription = newList[it]?.pokemonName,
                            modifier = Modifier
                                .size(120.dp)
                                .align(CenterHorizontally)
                        ) {
                            when (painter.state) {
                                is AsyncImagePainter.State.Loading -> {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.scale(0.5f)
                                    )
                                }

                                is AsyncImagePainter.State.Error -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Center
                                    ) {
                                        Text(
                                            text = "Image not available",
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                else -> {
                                    SubcomposeAsyncImageContent()
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = newList[it]?.pokemonName ?: "",
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


@Composable
fun RetryButton(error: String, onRetryClick: () -> Unit) {
    Column {
        Text(
            text = error,
            color = Color.Red,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onRetryClick() }, modifier = Modifier.align(CenterHorizontally)) {
            Text(text = "Retry")
        }
    }
}

