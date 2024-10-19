package com.example.comptesurleweb;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText login, password;
    Button button;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        button = findViewById(R.id.button);

        // Ouverture ou création de la base de données
        db = openOrCreateDatabase("compte", MODE_PRIVATE, null);

        // Création de la table USERS si elle n'existe pas déjà
        db.execSQL("CREATE TABLE IF NOT EXISTS USERS (login VARCHAR PRIMARY KEY, password VARCHAR);");

        // Vérification si la table USERS est vide
        SQLiteStatement s = db.compileStatement("SELECT COUNT(*) FROM USERS;");
        long count = s.simpleQueryForLong();

        // Insertion d'un utilisateur par défaut si la table est vide
        if (count == 0) {
            db.execSQL("INSERT INTO USERS (login, password) VALUES ('admin', '123');");
        }

        // Configuration du listener pour le bouton de connexion
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strLogin = login.getText().toString();
                String strPassword = password.getText().toString();

                // Requête pour récupérer le mot de passe correspondant au login saisi
                Cursor cur = db.rawQuery("SELECT password FROM USERS WHERE login=?", new String[]{strLogin});

                try {
                    if (cur.moveToFirst()) {
                        String storedPassword = cur.getString(0);

                        // Vérification du mot de passe
                        if (storedPassword.equals(strPassword)) {
                            Toast.makeText(MainActivity.this, "Bienvenue", Toast.LENGTH_SHORT).show();
                            // Démarrage de l'activité suivante (MainActivity2)
                            Intent i = new Intent(MainActivity.this, MainActivity2.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(MainActivity.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                            // Remise à zéro des champs login et mot de passe
                            login.setText("");
                            password.setText("");
                        }
                    } else {
                        // Si le login n'existe pas
                        Toast.makeText(MainActivity.this, "Login inexistant", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // En cas d'exception, affichage d'un message d'erreur
                    Toast.makeText(MainActivity.this, "Erreur lors de la vérification", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    // Fermeture du curseur pour éviter les fuites de mémoire
                    cur.close();
                }
            }
        });
    }
}
