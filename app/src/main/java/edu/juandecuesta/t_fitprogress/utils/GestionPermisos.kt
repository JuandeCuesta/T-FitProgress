package edu.juandecuesta.t_fitprogress.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//Clase para gestionar los distintos permisos
class GestionPermisos(
    val activity: Activity,
    val permiso: String,
    val code: Int
) {
    fun checkPermissions(): Boolean {
        // Se comprueba si el permiso en cuestión está concedido.
        if (ContextCompat.checkSelfPermission(
                activity,
                permiso
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no está concedido el permiso se entra.
            Log.d("DEBUG", "No tienes permiso para esta acción: $permiso")

            // Si el usuario ya lo ha rechazado al menos una vez (TRUE),
            // se puede mostrar una explicación.
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permiso)) {
                showAlert()
            } else {
                // No requiere explicación, se pregunta por el permiso.
                ActivityCompat.requestPermissions(activity, arrayOf(permiso), code)
            }
        } else {
            Log.d("DEBUG", "Permiso ($permiso) concedido!")
        }
        return ContextCompat.checkSelfPermission(
            activity,
            permiso
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Concesión de permisos")
        builder.setMessage(
            "Es necesario aceptar el permiso para: $permiso"
        )

        builder.setPositiveButton(android.R.string.ok) { _, _ ->

            ActivityCompat.requestPermissions(
                activity, arrayOf(permiso), code
            )
        }

        builder.setNeutralButton(android.R.string.cancel, null)
        builder.show()
    }
}