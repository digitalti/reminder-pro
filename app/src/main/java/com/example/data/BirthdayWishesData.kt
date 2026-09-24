package com.example.data

data class BirthdayWishTemplate(
  val id: String,
  val category: String, // Heartfelt, Funny, Short, Professional, Belated, Blessings
  val language: String = "English", // English, Spanish, French, German, Hindi, Italian
  val template: String
)

object BirthdayWishesData {
  val languages = listOf("English", "Spanish", "French", "German", "Hindi", "Italian")

  val categories = listOf(
    "All",
    "Warm & Heartfelt",
    "Funny & Humorous",
    "Short & Sweet",
    "Belated Wishes",
    "Professional",
    "Blessings"
  )

  val templates = listOf(
    // English - Heartfelt
    BirthdayWishTemplate(
      id = "en_hf_1",
      category = "Warm & Heartfelt",
      language = "English",
      template = "Happy Birthday, {name}! 🎉 Wishing you a day filled with endless love, laughter, and all your heart desires. May this year bring you boundless happiness!"
    ),
    BirthdayWishTemplate(
      id = "en_hf_2",
      category = "Warm & Heartfelt",
      language = "English",
      template = "Happy Birthday, dearest {name}! ❤️ Thank you for being such a shining light in my life. May your special day be as wonderful and kind as you are."
    ),
    BirthdayWishTemplate(
      id = "en_hf_3",
      category = "Warm & Heartfelt",
      language = "English",
      template = "To an amazing soul, Happy Birthday {name}! 🌟 Cherishing all the great memories we share and looking forward to making many more together."
    ),

    // English - Funny
    BirthdayWishTemplate(
      id = "en_fun_1",
      category = "Funny & Humorous",
      language = "English",
      template = "Happy Birthday, {name}! 🎂 Don't worry about getting older—you're not old, you're just a vintage classic! Cheers to another fabulous year!"
    ),
    BirthdayWishTemplate(
      id = "en_fun_2",
      category = "Funny & Humorous",
      language = "English",
      template = "Happy Birthday, {name}! 🎉 You know you're growing up when the candles cost more than the birthday cake! Have a wild celebration today!"
    ),
    BirthdayWishTemplate(
      id = "en_fun_3",
      category = "Funny & Humorous",
      language = "English",
      template = "Happy Birthday, {name}! 🥳 I was going to make you a rum cake, but now it's just cake and I'm drunk. Cheers to you, my favorite troublemaker!"
    ),

    // English - Short & Sweet
    BirthdayWishTemplate(
      id = "en_sh_1",
      category = "Short & Sweet",
      language = "English",
      template = "Happy Birthday, {name}! 🎂 Wishing you the happiest of days and a stellar year ahead!"
    ),
    BirthdayWishTemplate(
      id = "en_sh_2",
      category = "Short & Sweet",
      language = "English",
      template = "Cheers to another fantastic trip around the sun, {name}! 🎈 Enjoy your special day to the fullest!"
    ),
    BirthdayWishTemplate(
      id = "en_sh_3",
      category = "Short & Sweet",
      language = "English",
      template = "Happy Birthday, {name}! 🥳 Sending you big hugs, good vibes, and lots of celebration today!"
    ),

    // English - Belated (Crucial for Overdue Birthdays!)
    BirthdayWishTemplate(
      id = "en_bel_1",
      category = "Belated Wishes",
      language = "English",
      template = "Happy Belated Birthday, {name}! 🎂 Even though this wish is a little late, my heartfelt love and warmest wishes for you are always true. Hope you had a fantastic celebration!"
    ),
    BirthdayWishTemplate(
      id = "en_bel_2",
      category = "Belated Wishes",
      language = "English",
      template = "So sorry I missed your big day, {name}! 🎈 Consider this an extension of your birthday week! Wishing you a year ahead overflowing with joy and success."
    ),
    BirthdayWishTemplate(
      id = "en_bel_3",
      category = "Belated Wishes",
      language = "English",
      template = "Happy Belated Birthday, {name}! 🎉 Time got away from me, but you are always in my thoughts. Let's catch up soon and celebrate properly!"
    ),

    // English - Professional
    BirthdayWishTemplate(
      id = "en_prof_1",
      category = "Professional",
      language = "English",
      template = "Warmest birthday wishes, {name}! 🌟 It is an absolute privilege working with you. Wishing you continued personal success and happiness in the year ahead."
    ),
    BirthdayWishTemplate(
      id = "en_prof_2",
      category = "Professional",
      language = "English",
      template = "Happy Birthday, {name}! 💼 Thank you for your leadership, dedication, and positive energy. May this year bring you great achievements and prosperity."
    ),

    // English - Blessings
    BirthdayWishTemplate(
      id = "en_bless_1",
      category = "Blessings",
      language = "English",
      template = "Happy Birthday, {name}! 🙏 May you be blessed with abundant peace, vibrant health, and unending happiness on this special day and throughout the coming year."
    ),

    // Spanish (Español)
    BirthdayWishTemplate(
      id = "es_1",
      category = "Warm & Heartfelt",
      language = "Spanish",
      template = "¡Feliz Cumpleaños, {name}! 🎉 Que este nuevo año de vida te traiga abundante salud, amor, paz y todos los éxitos que te mereces."
    ),
    BirthdayWishTemplate(
      id = "es_2",
      category = "Funny & Humorous",
      language = "Spanish",
      template = "¡Feliz Cumpleaños, {name}! 🎂 ¡No te preocupes por hacerte mayor, te estás convirtiendo en un gran clásico!"
    ),
    BirthdayWishTemplate(
      id = "es_3",
      category = "Belated Wishes",
      language = "Spanish",
      template = "¡Feliz cumpleaños con retraso, {name}! 🎈 Mis mejores deseos para ti hoy y siempre. ¡Espero que hayas tenido un día maravilloso!"
    ),

    // French (Français)
    BirthdayWishTemplate(
      id = "fr_1",
      category = "Warm & Heartfelt",
      language = "French",
      template = "Joyeux Anniversaire, {name}! 🎂 Que cette nouvelle année t'apporte beaucoup de joie, de succès et de merveilleux moments inoubliables."
    ),
    BirthdayWishTemplate(
      id = "fr_2",
      category = "Short & Sweet",
      language = "French",
      template = "Très bel anniversaire à toi, {name}! 🥂 Santé, bonheur et prospérité pour toute cette nouvelle année!"
    ),

    // German (Deutsch)
    BirthdayWishTemplate(
      id = "de_1",
      category = "Warm & Heartfelt",
      language = "German",
      template = "Alles Gute zum Geburtstag, {name}! 🎉 Ich wünsche dir für dein neues Lebensjahr viel Glück, Gesundheit und unvergessliche Momente."
    ),
    BirthdayWishTemplate(
      id = "de_2",
      category = "Belated Wishes",
      language = "German",
      template = "Nachträglich alles Liebe und Gute zum Geburtstag, {name}! 🎈 Möge das neue Lebensjahr voller Freude und Erfolg sein!"
    ),

    // Hindi (हिंदी)
    BirthdayWishTemplate(
      id = "hi_1",
      category = "Warm & Heartfelt",
      language = "Hindi",
      template = "जन्मदिन की ढेर सारी शुभकामनाएं, {name}! 🎂 ईश्वर आपको हमेशा स्वस्थ, खुशहाल और दीर्घायु रखे। आपका यह साल मंगलमय हो!"
    ),
    BirthdayWishTemplate(
      id = "hi_2",
      category = "Short & Sweet",
      language = "Hindi",
      template = "हैप्पी बर्थडे, {name}! 🎉 आपका हर दिन खुशियों और नई सफलताओं से भरा रहे!"
    ),

    // Italian (Italiano)
    BirthdayWishTemplate(
      id = "it_1",
      category = "Warm & Heartfelt",
      language = "Italian",
      template = "Tanti auguri di buon compleanno, {name}! 🎂 Che questa giornata speciale ti porti tanta felicità, amore e sorrisi."
    )
  )

  fun formatWish(template: String, name: String, age: Int? = null): String {
    var result = template.replace("{name}", name)
    if (age != null) {
      result = result.replace("{age}", age.toString())
    }
    return result
  }

  fun getTemplatesFor(category: String, language: String): List<BirthdayWishTemplate> {
    return templates.filter {
      (language == "All" || it.language == language || (language != "English" && it.language == "English")) &&
      (category == "All" || it.category == category)
    }.sortedByDescending { it.language == language }
  }
}
