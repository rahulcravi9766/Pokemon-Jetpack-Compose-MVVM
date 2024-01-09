package com.example.pokemon.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.pokemon.data.models.PokemonListEntry
import com.example.pokemon.data.remote.response.PokemonDetail
import com.example.pokemon.data.remote.response.Type
import com.example.pokemon.utils.Resources
import com.example.pokemon.utils.parseTypeToColor
import com.example.pokemon.viewmodel.PokemonDetailViewModel
import com.example.pokemon.viewmodel.PokemonListViewModel
import kotlin.math.round
import com.example.pokemon.R

@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    navController: NavController,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {

    // produceState: used to fetch data from external sources and represent its loading, success, or error states
    val pokemonInfo = produceState<Resources<PokemonDetail>>(initialValue = Resources.Loading()) {
        value = viewModel.getPokemonDetails(pokemonName)
    }.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .background(color = dominantColor)
    ) {

        TopSection(
            navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.TopCenter)
        )
        PokemonDetailStateWrapper(
            pokemonInfo, modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )

        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            if (pokemonInfo is Resources.Success) {
                pokemonInfo.data?.sprites?.let {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y = topPadding),
                        model = it.frontDefault,
                        contentDescription = pokemonInfo.data.name,

                        ) {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
        }
    }
}

@Composable
fun TopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color.Black,
                    Color.Transparent
                )
            )
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Go back",
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resources<PokemonDetail>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {

    when (pokemonInfo) {
        is Resources.Success -> {

            PokemonDetailsSection(pokemonInfo.data!!, modifier = modifier.offset(y = ((-20).dp)) )
        }

        is Resources.Error -> {
            Text(text = pokemonInfo.message!!, color = Color.Red, modifier = modifier)
        }

        is Resources.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )
        }

    }
}

@Composable
fun PokemonDetailsSection(
    pokemonInfo: PokemonDetail,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "${pokemonInfo.id} ${pokemonInfo.name?.capitalize(Locale.current)}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        PokemonTypeSection(types = pokemonInfo.types ?: listOf())
        PokemonDataSection(pokemonWeight = pokemonInfo.weight ?: 0, pokemonHeight = pokemonInfo.height ?: 0)
    }
}

@Composable
fun PokemonTypeSection(types: List<Type>) {

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        for (type in types) {
            Box(
                contentAlignment =  Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(color = parseTypeToColor(type))
                    .height(35.dp)
            ) {
                Text(text = type.type?.name?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        java.util.Locale.ROOT
                    ) else it.toString()
                }
                    ?: "",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PokemonDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {

    val pokemonWeightInKg = remember {
        round(pokemonWeight * 100f) / 1000f
    }
    val pokemonHeightInMeter = remember {
        round(pokemonHeight * 100f) / 1000f
    }

    Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()){
        PokemonDataItem(dataValue = pokemonWeightInKg, dataUnit = "kg", dataIcon = painterResource(
            id = R.drawable.weight
        ), modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(5.dp))
        Divider(modifier = Modifier
            .width(2.dp)
            .height(sectionHeight)
            .background(color = Color.Black)
            )
        Spacer(modifier = Modifier.width(5.dp))
        PokemonDataItem(dataValue = pokemonHeightInMeter, dataUnit = "m", dataIcon = painterResource(
            id = R.drawable.baseline_height_24
        ), modifier = Modifier.weight(1f))
    }
}

@Composable
fun PokemonDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {

        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = modifier.height(8.dp))
        Text(text = "$dataValue$dataUnit", color = MaterialTheme.colorScheme.onSurface)
    }
}


