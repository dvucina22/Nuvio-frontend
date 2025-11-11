package com.example.nuviofrontend.feature.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import com.example.nuviofrontend.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nuviofrontend.core.ui.theme.ColorInput
import com.example.nuviofrontend.core.ui.theme.DirtyWhite
import com.example.nuviofrontend.core.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(275.dp),
                drawerContainerColor = Color(0xFF1A1F16)
            ) {
                DrawerHeader()
                DrawerItem(Icons.Default.LocalOffer, "Proizvodi")
                DrawerItem(Icons.Default.Favorite, "Favoriti")
                DrawerItem(Icons.Default.ShoppingCart, "Košarica")
                DrawerItem(Icons.Default.ReceiptLong, "Povijest narudžbi")
                DrawerItem(Icons.Default.Person, "Moj profil")
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_dark),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { scope.launch { drawerState.open() } },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = White
                )
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Početni zaslon", color = White)
            }
        }
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_dark_icon),
                contentDescription = "Logo",
                modifier = Modifier.size(59.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Nuvio",
                style = MaterialTheme.typography.titleLarge,
                color = DirtyWhite,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF5C6B73))
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
        Text(
            text = "Pozdrav, {ime}",
            style = MaterialTheme.typography.titleMedium,
            color = DirtyWhite,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 11.dp).padding(bottom = 24.dp).padding(start = 16.dp)
        )
    }
}


@Composable
fun DrawerItem(icon: ImageVector, label: String) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = label, tint = ColorInput) },
        label = { Text(label, color = DirtyWhite, style = MaterialTheme.typography.titleSmall) },
        selected = false,
        onClick = { /* TODO: navigacija */ },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}