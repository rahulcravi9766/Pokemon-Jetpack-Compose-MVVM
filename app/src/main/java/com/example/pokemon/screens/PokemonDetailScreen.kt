package com.example.pokemon.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pokemon.data.models.PokemonListEntry
import com.example.pokemon.viewmodel.PokemonListViewModel

@Composable
fun PokemonDetailScreen(
    detail: PokemonListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {

}