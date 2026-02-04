package fr.epechassieu.carnetdechant.domain.model

/**
 * Represents the thematic or liturgical categories used to classify songs within the songbook.
 * Each category contains a human-readable label ([libelle]) used for display in the user interface.
 */
enum class Category(val libelle: String) {
    ACCUEIL("Accueil"),
    ADORATION("Adoration"),
    APPEL("Appel"),
    COMBAT_SPIRITUEL("Combat spirituel"),
    CONFIANCE("Confiance"),
    CONSECRATION("Consécration"),
    DIEU("Dieu (Éternel-Père)"),
    EGLISE("Église"),
    ENCOURAGEMENT("Encouragement"),
    ENVOI("Envoi"),
    FOI("Foi"),
    GRACE("Grâce"),
    JESUS("Jésus"),
    LOUANGE("Louange"),
    NOEL("Noël"),
    PAQUES("Pâques"),
    PRIERE("Prière"),
    PARDON("Pardon"),
    PAROLE("Parole"),
    RECONNAISSANCE("Reconnaissance"),
    REPENTANCE("Repentance"),
    SAINT_ESPRIT("Saint-Esprit"),
    SAINTE_CENE("Sainte Cène"),
    SALUT("Salut"),
    UNITE("Unité"),
    INCONNU("Inconnu")
}