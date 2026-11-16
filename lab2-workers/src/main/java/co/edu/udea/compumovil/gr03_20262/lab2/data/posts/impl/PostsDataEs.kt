/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("ktlint:standard:max-line-length") // String constants read better

package co.edu.udea.compumovil.gr03_20262.lab2.data.posts.impl

import co.edu.udea.compumovil.gr03_20262.lab2.R
import co.edu.udea.compumovil.gr03_20262.lab2.model.Metadata
import co.edu.udea.compumovil.gr03_20262.lab2.model.Paragraph
import co.edu.udea.compumovil.gr03_20262.lab2.model.ParagraphType
import co.edu.udea.compumovil.gr03_20262.lab2.model.Post
import co.edu.udea.compumovil.gr03_20262.lab2.model.PostsFeed

/**
 * Spanish version of the hardcoded posts, used when the app language is Spanish.
 */

val paragraphsPost1Es = listOf(
    Paragraph(
        ParagraphType.Text,
        "Trabajando para hacer que nuestra aplicación de Android fuera más modular, terminé con una muestra que incluía un conjunto de funciones bajo demanda agrupadas dentro de una carpeta:",
    ),
    Paragraph(
        ParagraphType.Text,
        "Una configuración bastante estándar, todos los módulos bajo demanda dentro de una carpeta «features»; limpio.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Estos módulos se incluyen en el archivo settings.gradle como:",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        "include ':app'\n" +
            "include ':features:module1'\n" +
            "include ':features:module2'\n" +
            "include ':features:module3'\n" +
            "include ':features:module4'",
    ),
    Paragraph(
        ParagraphType.Text,
        "Esta configuración funciona bien con un único problema «menor»: un módulo vacío llamado features en la vista de Android de Android Studio:",
    ),
    Paragraph(
        ParagraphType.Text,
        "Puedo vivir con eso, pero ¡preferiría mucho eliminar ese módulo vacío de mi proyecto!",
    ),
    Paragraph(
        ParagraphType.Header,
        "¡Si no puedes eliminarlo, ¡renómbralo!",
    ),
    Paragraph(
        ParagraphType.Text,
        "En el I/O tuve la suerte de asistir a la charla «Android Studio: Tips and Tricks» donde Ivan Gravilovic, de Google, compartió algunos consejos increíbles. Uno de ellos era una posible solución para mi problema: definir una ruta personalizada para mis módulos.",
    ),
    Paragraph(
        ParagraphType.Text,
        "En este caso particular, nuestro settings.gradle queda así:",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        """
        include ':app'
        include ':module1'
        include ':module1'
        include ':module1'
        include ':module1'
        """.trimIndent(),
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        """
        // Set a custom path for the four features modules.
        // This avoid to have an empty "features" module in  Android Studio.
        project(":module1").projectDir=new File(rootDir, "features/module1")
        project(":module2").projectDir=new File(rootDir, "features/module2")
        project(":module3").projectDir=new File(rootDir, "features/module3")
        project(":module4").projectDir=new File(rootDir, "features/module4")
        """.trimIndent(),
    ),
    Paragraph(
        ParagraphType.Text,
        "Y el diseño en Android Studio ahora es:",
    ),
    Paragraph(
        ParagraphType.Header,
        "Conclusión",
    ),
    Paragraph(
        ParagraphType.Text,
        "Como dice el título, esto es realmente algo pequeño, pero ayuda a mantener mi proyecto en orden y muestra cómo una pequeña configuración de Gradle puede ayudar a mantener tu proyecto ordenado.",
    ),
    Paragraph(
        ParagraphType.Quote,
        "Puedes encontrar esta actualización en la versión más reciente del codelab de módulos bajo demanda.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Recursos",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Android Studio: Tips and Tricks (Google I/O'19)",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Codelab de módulos bajo demanda",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Patchwork Plaid — Una historia de modularización",
    ),
)

val paragraphsPost2Es = listOf(
    Paragraph(
        ParagraphType.Text,
        "Dagger es un popular framework de inyección de dependencias comúnmente usado en Android. Proporciona dependencias totalmente estáticas y en tiempo de compilación, abordando muchos de los problemas de desarrollo y rendimiento que tienen las soluciones basadas en reflexión.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Este mes se publicó un nuevo tutorial para ayudarte a entender mejor cómo funciona. Este artículo se centra en el uso de Dagger con Kotlin, incluyendo buenas prácticas para optimizar tu tiempo de compilación y trampas que podrías encontrar.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Dagger está implementado usando el modelo de anotaciones de Java y las anotaciones en Kotlin no siempre son directamente paralelas a cómo se escribiría el código equivalente en Java. Esta publicación destacará las áreas donde difieren y cómo puedes usar Dagger con Kotlin sin dolores de cabeza.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Esta publicación fue inspirada por algunas de las sugerencias en este issue de Dagger que repasa las buenas prácticas y los puntos dolorosos de Dagger en Kotlin. ¡Gracias a todos los colaboradores que comentaron allí!",
    ),
    Paragraph(
        ParagraphType.Header,
        "Mejoras de compilación con kapt",
    ),
    Paragraph(
        ParagraphType.Text,
        "Para mejorar tu tiempo de compilación, ¡Dagger agregó soporte para el procesamiento incremental de anotaciones de Gradle en la v2.18! Esto está habilitado por defecto en Dagger v2.24. En caso de que uses una versión inferior, necesitas agregar unas líneas de código (como se muestra abajo) si quieres beneficiarte de ello.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Además, puedes decirle a Dagger que no formatee el código generado. Esta opción se agregó en Dagger v2.18 y es el comportamiento predeterminado (no genera código formateado) en la v2.23. Si usas una versión inferior, desactiva el formateo de código para mejorar tu tiempo de compilación (ver código abajo).",
    ),
    Paragraph(
        ParagraphType.Text,
        "Incluye estos argumentos del compilador en tu archivo build.gradle para hacer que Dagger sea más eficiente en tiempo de compilación:",
    ),
    Paragraph(
        ParagraphType.Text,
        "Alternativamente, si usas archivos de script Kotlin DSL, inclúyelos así en el archivo build.gradle.kts de los módulos que usan Dagger:",
    ),
    Paragraph(
        ParagraphType.Text,
        "Qualifiers para atributos de campo",
    ),
    Paragraph(
        ParagraphType.Text,
        "Cuando una anotación se coloca en una propiedad en Kotlin, no está claro si Java verá esa anotación en el campo de la propiedad o en el método de esa propiedad. Agregar el prefijo field: a la anotación asegura que el qualifier quede en el lugar correcto (ver la documentación para más detalles).",
    ),
    Paragraph(
        ParagraphType.Text,
        "✅ La forma de aplicar qualifiers en un campo inyectado es:",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        "@Inject @field:MinimumBalance lateinit var minimumBalance: BigDecimal",
    ),
    Paragraph(
        ParagraphType.Text,
        "❌ A diferencia de:",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        """
        @Inject @MinimumBalance lateinit var minimumBalance: BigDecimal 
        // @MinimumBalance is ignored!
        """.trimIndent(),
    ),
    Paragraph(
        ParagraphType.Text,
        "Olvidar agregar field: puede llevar a inyectar el objeto incorrecto si hay una instancia sin calificar de ese tipo disponible en el grafo de Dagger.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Optimización de métodos @Provides estáticos",
    ),
    Paragraph(
        ParagraphType.Text,
        "El código generado por Dagger será más eficiente si los métodos @Provides son estáticos. Para lograrlo en Kotlin, usa un object de Kotlin en lugar de una class y anota tus métodos con @JvmStatic. Esta es una buena práctica que deberías seguir en la medida de lo posible.",
    ),
    Paragraph(
        ParagraphType.Text,
        "En caso de que necesites un método abstracto, tendrás que agregar el método @JvmStatic a un companion object y anotarlo también con @Module.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Alternativamente, puedes extraer el módulo object y incluirlo en el abstracto:",
    ),
    Paragraph(
        ParagraphType.Header,
        "Inyección de genéricos",
    ),
    Paragraph(
        ParagraphType.Text,
        "Kotlin compila los genéricos con wildcards para que las APIs de Kotlin funcionen con Java. Estos se generan cuando un tipo aparece como parámetro (más información aquí) o como campo. Por ejemplo, un parámetro List<Foo> de Kotlin aparece como List<? super Foo> en Java.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Esto causa problemas con Dagger porque espera una coincidencia de tipo exacta (es decir, invariante). Usar @JvmSuppressWildcards asegurará que Dagger vea el tipo sin wildcards.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Este es un problema común cuando inyectas colecciones usando la función de multibinding de Dagger, por ejemplo:",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        """
        class MyVMFactory @Inject constructor(
          private val vmMap: Map<String, @JvmSuppressWildcards Provider<ViewModel>>
        ) { 
            ... 
        }
        """.trimIndent(),
    ),
    Paragraph(
        ParagraphType.Header,
        "Cuerpos de métodos en línea",
    ),
    Paragraph(
        ParagraphType.Text,
        "Dagger determina los tipos configurados por los métodos @Provides inspeccionando el tipo de retorno. Especificar el tipo de retorno en las funciones de Kotlin es opcional e incluso el IDE a veces te anima a refactorizar tu código para tener cuerpos de métodos en línea que ocultan la declaración del tipo de retorno.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Esto puede llevar a errores si el tipo inferido es diferente del que pretendías. Veamos algunos ejemplos:",
    ),
    Paragraph(
        ParagraphType.Text,
        "Si quieres agregar un tipo específico al grafo, el inline funciona como se espera. Mira las diferentes formas de hacer lo mismo en Kotlin:",
    ),
    Paragraph(
        ParagraphType.Text,
        "Si quieres proporcionar una implementación de una interfaz, entonces debes especificar explícitamente el tipo de retorno. No hacerlo puede llevar a problemas y errores:",
    ),
    Paragraph(
        ParagraphType.Text,
        "Dagger funciona con Kotlin sin problemas en la mayoría de los casos. Sin embargo, debes tener cuidado con algunas cosas para asegurarte de hacer lo que realmente quieres: @field: para qualifiers en atributos de campo, cuerpos de métodos en línea y @JvmSuppressWildcards al inyectar colecciones.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Las optimizaciones de Dagger no tienen costo, agrégalas y sigue las buenas prácticas para mejorar tu tiempo de compilación: habilitar el procesamiento incremental de anotaciones, desactivar el formateo y usar métodos @Provides estáticos en tus módulos de Dagger.",
    ),
)

val paragraphsPost3Es = listOf(
    Paragraph(
        ParagraphType.Text,
        "Aprende a comenzar a convertir código del lenguaje de programación Java a Kotlin, haciéndolo más idiomático y evitando trampas comunes, siguiendo nuestro nuevo codelab Refactoring to Kotlin, disponible en inglés, chino y portugués brasileño.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Cuando empiezas a escribir código en Kotlin, tiendes a seguir los idiomas del lenguaje de programación Java. El conversor automático, parte tanto de Android Studio como de IntelliJ IDEA, puede hacer un muy buen trabajo refactorizando tu código automáticamente, pero a veces necesita un poco de ayuda. Aquí es donde entra nuestro nuevo codelab Refactoring to Kotlin.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Tomaremos dos clases (un User y un Repository) en lenguaje de programación Java y las convertiremos a Kotlin, veremos qué hizo el conversor automático y por qué. Luego pasaremos al siguiente nivel: hacerlo idiomático, enseñando buenas prácticas y consejos útiles en el camino.",
    ),
    Paragraph(
        ParagraphType.Text,
        "El codelab Refactoring to Kotlin comienza con temas básicos: entender cómo se declara la nulabilidad en Kotlin, qué tipos de igualdad están definidos o cómo manejar mejor las clases cuyo rol es solo contener datos. Luego continuamos con cómo manejar campos y funciones estáticas en Kotlin y cómo aplicar el patrón Singleton, con la ayuda de una palabra clave útil: object. Veremos cómo Kotlin nos ayuda a modelar mejor nuestras clases, cómo diferencia entre una propiedad de una clase y una acción que la clase puede realizar. Finalmente, aprenderemos cómo ejecutar código solo en el contexto de un objeto específico con las scope functions.",
    ),
    Paragraph(
        ParagraphType.Text,
        "¡Gracias a Walmyr Carvalho y Nelson Glauber por traducir el codelab al portugués brasileño!",
    ),
)

val post1Es = Post(
    id = "dc523f0ed25c",
    title = "Una pequeña cosa sobre las rutas de módulos de Android",
    subtitle = "Cómo configurar las rutas de tus módulos, en lugar de usar las rutas predeterminadas de Gradle.",
    url = "https://medium.com/androiddevelopers/gradle-path-configuration-dc523f0ed25c",
    publication = publication,
    metadata = Metadata(
        author = pietro,
        date = "2 de agosto",
        readTimeMinutes = 1,
    ),
    paragraphs = paragraphsPost1Es,
    imageId = R.drawable.post_1,
    imageThumbId = R.drawable.post_1_thumb,
)

val post2Es = Post(
    id = "7446d8dfd7dc",
    title = "Dagger en Kotlin: trampas y optimizaciones",
    subtitle = "¡Usa Dagger en Kotlin! Este artículo incluye buenas prácticas para optimizar tu tiempo de compilación y trampas que podrías encontrar.",
    url = "https://medium.com/androiddevelopers/dagger-in-kotlin-gotchas-and-optimizations-7446d8dfd7dc",
    publication = publication,
    metadata = Metadata(
        author = manuel,
        date = "30 de julio",
        readTimeMinutes = 3,
    ),
    paragraphs = paragraphsPost2Es,
    imageId = R.drawable.post_2,
    imageThumbId = R.drawable.post_2_thumb,
)

val post3Es = Post(
    id = "ac552dcc1741",
    title = "Del lenguaje de programación Java a Kotlin — la forma idiomática",
    subtitle = "Aprende a comenzar a convertir código del lenguaje de programación Java a Kotlin, haciéndolo más idiomático y evitando trampas comunes, con…",
    url = "https://medium.com/androiddevelopers/from-java-programming-language-to-kotlin-the-idiomatic-way-ac552dcc1741",
    publication = publication,
    metadata = Metadata(
        author = florina,
        date = "9 de julio",
        readTimeMinutes = 1,
    ),
    paragraphs = paragraphsPost3Es,
    imageId = R.drawable.post_3,
    imageThumbId = R.drawable.post_3_thumb,
)

val postsEs: PostsFeed =
    PostsFeed(
        highlightedPost = post3Es,
        recommendedPosts = listOf(post1Es, post2Es, post3Es),
        popularPosts = listOf(
            post1Es,
            post2Es,
        ),
        recentPosts = listOf(
            post3Es,
            post1Es,
        ),
    )
