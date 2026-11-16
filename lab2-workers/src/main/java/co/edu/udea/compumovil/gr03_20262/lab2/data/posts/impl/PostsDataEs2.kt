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

val paragraphsPost4Es = listOf(
    Paragraph(
        ParagraphType.Text,
        "TL;DR: Expón los IDs de recursos desde los ViewModels para evitar mostrar datos obsoletos.",
    ),
    Paragraph(
        ParagraphType.Text,
        "En un ViewModel, si expones datos que vienen de recursos (strings, drawables, colores…), tienes que tener en cuenta que los objetos ViewModel ignoran los cambios de configuración como los cambios de locale. Cuando el usuario cambia su locale, las activities se recrean pero los objetos ViewModel no.",
    ),
    Paragraph(
        ParagraphType.Text,
        "AndroidViewModel es una subclase de ViewModel que conoce el contexto de la Application. Sin embargo, tener acceso a un contexto puede ser peligroso si no estás observando o reaccionando al ciclo de vida de ese contexto. La práctica recomendada es evitar tratar con objetos que tienen un ciclo de vida en los ViewModels.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Veamos un ejemplo basado en este issue del tracker: Actualización de ViewModel al cambiar el locale del sistema.",
    ),
    Paragraph(
        ParagraphType.Text,
        "El problema es que el string se resuelve en el constructor solo una vez. Si hay un cambio de locale, el ViewModel no se recreará. Esto resultará en que nuestra app muestre datos obsoletos y por lo tanto esté solo parcialmente localizada.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Como señala Sergey en los comentarios del issue, el enfoque recomendado es exponer el ID del recurso que quieres cargar y hacerlo en la vista. Como la vista (activity, fragment, etc.) es consciente del ciclo de vida, se recreará después de un cambio de configuración, por lo que el recurso se recargará correctamente.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Incluso si no planeas localizar tu app, hace las pruebas mucho más fáciles y limpia tus objetos ViewModel, así que no hay razón para no prepararte para el futuro.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Corregimos este issue en el repositorio android-architecture en las ramas de Java y Kotlin y trasladamos la carga de recursos al layout de Data Binding.",
    ),
)

val paragraphsPost5Es = listOf(
    Paragraph(
        ParagraphType.Text,
        "Trabajar con colecciones es una tarea común y la Kotlin Standard Library ofrece muchas funciones de utilidad geniales. También ofrece dos formas de trabajar con colecciones según cómo se evalúan: eagerly — con Collections, y lazily — con Sequences. Continúa leyendo para descubrir cuál es la diferencia entre las dos, cuál deberías usar y cuándo, y cuáles son las implicaciones de rendimiento de cada una.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Collections vs sequences",
    ),
    Paragraph(
        ParagraphType.Text,
        "La diferencia entre la evaluación eager y lazy radica en cuándo se realiza cada transformación en la colección.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Las colecciones se evalúan de forma eager — cada operación se realiza cuando se llama y el resultado de la operación se almacena en una nueva colección. Las transformaciones en colecciones son funciones inline. Por ejemplo, mirando cómo está implementado map, podemos ver que es una función inline que crea un nuevo ArrayList:",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        "public inline fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R> {\n" +
            "  return mapTo(ArrayList<R>(collectionSizeOrDefault(10)), transform)\n" +
            "}",
    ),
    Paragraph(
        ParagraphType.Text,
        "Las secuencias se evalúan de forma lazy. Tienen dos tipos de operaciones: intermedias y terminales. Las operaciones intermedias no se realizan en el momento; solo se almacenan. Solo cuando se llama a una operación terminal, las operaciones intermedias se disparan en cada elemento una tras otra y finalmente se aplica la operación terminal. Las operaciones intermedias (como map, distinct, groupBy, etc.) devuelven otra secuencia, mientras que las operaciones terminales (como first, toList, count, etc.) no.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Las secuencias no mantienen una referencia a los elementos de la colección. Se crean basadas en el iterador de la colección original y mantienen una referencia a todas las operaciones intermedias que deben realizarse.",
    ),
    Paragraph(
        ParagraphType.Text,
        "A diferencia de las transformaciones en colecciones, las transformaciones intermedias en secuencias no son funciones inline — las funciones inline no pueden almacenarse y las secuencias necesitan almacenarlas. Mirando cómo está implementada una operación intermedia como map, podemos ver que la función transform se guarda en una nueva instancia de una Sequence:",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        "public fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R>{      \n" +
            "   return TransformingSequence(this, transform)\n" +
            "}",
    ),
    Paragraph(
        ParagraphType.Text,
        "Una operación terminal, como first, itera a través de los elementos de la secuencia hasta que se cumple la condición del predicado.",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        "public inline fun <T> Sequence<T>.first(predicate: (T) -> Boolean): T {\n" +
            "   for (element in this) if (predicate(element)) return element\n" +
            "   throw NoSuchElementException(“Sequence contains no element matching the predicate.”)\n" +
            "}",
    ),
    Paragraph(
        ParagraphType.Text,
        "Si miramos cómo está implementada una secuencia como TransformingSequence (usada en el map anterior), veremos que cuando se llama a next en el iterador de la secuencia, también se aplica la transformación almacenada.",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        "internal class TransformingIndexedSequence<T, R> \n" +
            "constructor(private val sequence: Sequence<T>, private val transformer: (Int, T) -> R) : Sequence<R> {",
    ),
    Paragraph(
        ParagraphType.CodeBlock,
        "override fun iterator(): Iterator<R> = object : Iterator<R> {\n" +
            "   …\n" +
            "   override fun next(): R {\n" +
            "     return transformer(checkIndexOverflow(index++), iterator.next())\n" +
            "   }\n" +
            "   …\n" +
            "}",
    ),
    Paragraph(
        ParagraphType.Text,
        "Independientemente de si usas colecciones o secuencias, la Kotlin Standard Library ofrece una amplia gama de operaciones para ambas, como find, filter, groupBy y otras. Asegúrate de revisarlas antes de implementar tu propia versión de estas.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Colecciones y secuencias",
    ),
    Paragraph(
        ParagraphType.Text,
        "Supongamos que tenemos una lista de objetos de diferentes formas. Queremos poner las formas de color amarillo y luego tomar la primera forma cuadrada.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Veamos cómo y cuándo se aplica cada operación para colecciones y cuándo para secuencias",
    ),
    Paragraph(
        ParagraphType.Subhead,
        "Colecciones",
    ),
    Paragraph(
        ParagraphType.Text,
        "map es llamado — se crea un nuevo ArrayList. Iteramos a través de todos los elementos de la colección inicial, lo transformamos copiando el objeto original y cambiando el color, luego lo agregamos a la nueva lista.",
    ),
    Paragraph(
        ParagraphType.Text,
        "first es llamado — iteramos a través de cada elemento hasta encontrar el primer cuadrado",
    ),
    Paragraph(
        ParagraphType.Subhead,
        "Secuencias",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "asSequence — se crea una secuencia basada en el Iterator de la colección original",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "map es llamado — la transformación se agrega a la lista de operaciones que la secuencia necesita realizar, pero la operación NO se realiza",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "first es llamado — esta es una operación terminal, así que todas las operaciones intermedias se disparan, en cada elemento de la colección. Iteramos a través de la colección inicial aplicando map y luego first en cada uno de ellos. Como la condición de first se satisface con el 2º elemento, ya no aplicamos map al resto de la colección.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Cuando trabajas con secuencias no se crea ninguna colección intermedia y como los elementos se evalúan uno por uno, map solo se realiza en algunos de los datos de entrada.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Rendimiento",
    ),
    Paragraph(
        ParagraphType.Subhead,
        "Orden de las transformaciones",
    ),
    Paragraph(
        ParagraphType.Text,
        "Independientemente de si usas colecciones o secuencias, el orden de las transformaciones importa. En el ejemplo anterior, first no necesita ocurrir después de map ya que no es una consecuencia de la transformación map. Si invertimos el orden de nuestra lógica de negocio y llamamos a first en la colección y luego transformamos el resultado, entonces solo creamos un nuevo objeto: el cuadrado amarillo. Al usar secuencias evitamos crear 2 nuevos objetos; al usar colecciones, evitamos crear una lista completamente nueva.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Como las operaciones terminales pueden terminar el procesamiento antes de tiempo, y las operaciones intermedias se evalúan de forma lazy, las secuencias pueden, en algunos casos, ayudarte a evitar hacer trabajo innecesario en comparación con las colecciones. ¡Asegúrate de revisar siempre el orden de las transformaciones y las dependencias entre ellas!",
    ),
    Paragraph(
        ParagraphType.Subhead,
        "Inlining y consecuencias con conjuntos de datos grandes",
    ),
    Paragraph(
        ParagraphType.Text,
        "Las operaciones de colecciones usan funciones inline, por lo que el bytecode de la operación, junto con el bytecode del lambda que se le pasa, se incrustará. Las secuencias no usan funciones inline, por lo tanto, se crean nuevos objetos Function para cada operación.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Por otro lado, las colecciones crean una nueva lista para cada transformación mientras que las secuencias solo mantienen una referencia a la función de transformación.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Cuando trabajas con colecciones pequeñas, con 1–2 operadores, estas diferencias no tienen grandes implicaciones, así que trabajar con colecciones debería estar bien. Pero, cuando trabajas con listas grandes, la creación de colecciones intermedias puede volverse costosa; en esos casos, usa secuencias.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Desafortunadamente, no conozco ningún estudio de benchmarking que se haya hecho que nos ayude a entender mejor cómo se ve afectado el rendimiento de colecciones vs secuencias con diferentes tamaños de colecciones o cadenas de operaciones.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Las colecciones evalúan tus datos de forma eager mientras que las secuencias lo hacen de forma lazy. Dependiendo del tamaño de tus datos, elige la que mejor se adapte: colecciones — para listas pequeñas o secuencias — para las más grandes, y presta atención al orden de las transformaciones.",
    ),
)

val paragraphsPost6Es = listOf(
    Paragraph(
        ParagraphType.Text,
        "El rediseño del logo de Android Studio captó la atención de la comunidad de desarrolladores desde su adelanto en el Android Developer Summit. Estamos encantados de lanzar el nuevo logo de Android Studio con la versión estable de Flamingo. Ahora que el nuevo logo está disponible para la mayoría de los usuarios de Android Studio, podemos examinar los cambios de diseño con más detalle y descifrar su significado.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Este caso de estudio ofrece una visión general completa del viaje de diseño, desde la identificación del problema inicial hasta el resultado final. Explora los elementos clave de la marca que el equipo necesitaba considerar y las herramientas utilizadas durante todo el proceso de rediseño. Este caso de estudio también profundiza en las diversas etapas de exploración del diseño, destacando los esfuerzos por crear un logo moderno mientras se honra el legado de la marca Android Studio.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Identificando el problema",
    ),
    Paragraph(
        ParagraphType.Text,
        "Nos dijeron que el logo de Android Studio se veía un poco raro y complicado. No se reduce bien y es demasiado similar al emulador. ¡Los escuchamos!",
    ),
    Paragraph(
        ParagraphType.Text,
        "El logo de Android Studio usado entre 2020 y 2022 era adecuado para impresión, pero planteaba desafíos cuando se usaba como ícono de aplicación. Su legibilidad sufría al reducirse a tamaños más pequeños, y su similitud con el emulador causaba confusión.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Además, el uso del color por sí solo para diferenciar entre las versiones Canary y Stable dificultaba que los usuarios con deficiencias en la visión del color las distinguieran.",
    ),
    Paragraph(
        ParagraphType.Text,
        "El rediseño buscaba resolver estas preocupaciones creando un logo que fuera fácil de leer, visualmente distintivo y que siguiera las pautas del SO cuando fuera necesario, asegurando la accesibilidad. El nuevo diseño también mantuvo una conexión con la familia de logos de Android mientras honraba su legado.",
    ),
    Paragraph(
        ParagraphType.Text,
        "En este caso de estudio, profundizaremos en el historial de versiones y la evolución del logo de Android Studio y cómo ha cambiado a lo largo de los años.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Una breve historia del logo de Android Studio",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2013: El logo original de Android Studio era un robot 3D que destacaba los engranajes y el funcionamiento interno del bugdroid. En ese momento, el Android Emulator era el bugdroid.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2014: El Android Emulator pasó a una marca plana pero permaneció sin cambios en lo demás.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2014-2019: Se introdujo un logo actualizado de Android Studio con una brújula «A» frente a un círculo verde.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2019: En Canary 3.6, la paleta de colores se actualizó para coincidir con Android 10.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "2020-2022: Con el lanzamiento de Android Studio 4.1 Canary, la brújula «A» se redujo a una forma abstracta colocada frente a un plano. También se agregó la cabeza de Android, asomándose por arriba.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Comprendiendo los elementos de la marca Android",
    ),
    Paragraph(
        ParagraphType.Text,
        "Al rediseñar un logo, es importante considerar los elementos de la marca que unifican los productos dentro de un ecosistema. Para el ecosistema de Android Developer, la «cabeza de robot» es un elemento clave de la marca, junto con el color verde primario de Android. Los colores secundarios azul y navy, y los colores terciarios como el naranja, también pueden utilizarse como apoyo.",
    ),
    Paragraph(
        ParagraphType.Header,
        "Objetivos clave",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Iconografía: usar símbolos reconocibles y apropiados, como la brújula «A» para Android Studio o un dispositivo para Android Emulator, para transmitir el propósito y la funcionalidad de manera clara y rápida.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Mejorar el reconocimiento y la escalabilidad: Android Studio y Android Emulator deben priorizar la legibilidad y la escalabilidad, asegurando que puedan reconocerse y entenderse fácilmente incluso en tamaños más pequeños.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Establecer distinción: Android Studio y Android Emulator necesitan ser fácilmente distinguibles para evitar confusión.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Mantener la consistencia de marca: los diseños de Android Studio y Android Emulator deben ser consistentes con la marca general y la identidad visual de la familia Android, manteniéndose distintivos.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Asegurar la accesibilidad: el logo debe ser accesible para todos los usuarios, incluidos aquellos con discapacidades visuales. Esto significa usar formas, colores y contraste claros.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Seguir las pautas del SO: el ícono de aplicación actualizado debe alinearse con el lenguaje visual de Android y conformarse a las pautas de los sistemas operativos macOS, Windows y Linux, asegurando consistencia y coherencia en todas las plataformas.",
    ),
    Paragraph(
        ParagraphType.Bullet,
        "Asegurar la versatilidad: el logo de Android Studio debe ser lo suficientemente versátil para funcionar en una variedad de tamaños y contextos, como en diferentes dispositivos y plataformas.",
    ),
    Paragraph(
        ParagraphType.Text,
        "Leer más",
    ),
)

val post4Es = Post(
    id = "84eb677660d9",
    title = "Cambios de locale y el antipatrón de AndroidViewModel",
    subtitle = "TL;DR: Expón los IDs de recursos desde los ViewModels para evitar mostrar datos obsoletos.",
    url = "https://medium.com/androiddevelopers/locale-changes-and-the-androidviewmodel-antipattern-84eb677660d9",
    publication = publication,
    metadata = Metadata(
        author = jose,
        date = "2 de abril",
        readTimeMinutes = 1,
    ),
    paragraphs = paragraphsPost4Es,
    imageId = R.drawable.post_4,
    imageThumbId = R.drawable.post_4_thumb,
)

val post5Es = Post(
    id = "55db18283aca",
    title = "Colecciones y secuencias en Kotlin",
    subtitle = "Trabajar con colecciones es una tarea común y la Kotlin Standard Library ofrece muchas funciones de utilidad geniales. También ofrece dos formas de…",
    url = "https://medium.com/androiddevelopers/collections-and-sequences-in-kotlin-55db18283aca",
    publication = publication,
    metadata = Metadata(
        author = florina,
        date = "24 de julio",
        readTimeMinutes = 4,
    ),
    paragraphs = paragraphsPost5Es,
    imageId = R.drawable.post_5,
    imageThumbId = R.drawable.post_5_thumb,
)

val post6Es = Post(
    id = "55db18283ac0",
    title = "Rediseñando el logo de Android Studio",
    subtitle = "Un caso de estudio que ofrece una visión general completa del viaje de diseño del logo del producto Android Studio.",
    url = "https://android-developers.googleblog.com/2023/05/redesigning-android-studio-logo.html",
    publication = publication,
    metadata = Metadata(
        author = androidstudioteam,
        date = "10 de mayo",
        readTimeMinutes = 5,
    ),
    paragraphs = paragraphsPost6Es,
    imageId = R.drawable.post_6,
    imageThumbId = R.drawable.post_6_thumb,
)

val postsEs: PostsFeed =
    PostsFeed(
        highlightedPost = post6Es,
        recommendedPosts = listOf(post1Es, post2Es, post3Es),
        popularPosts = listOf(
            post5Es,
            post1Es.copy(id = "post6"),
            post2Es.copy(id = "post7"),
        ),
        recentPosts = listOf(
            post6Es,
            post3Es.copy(id = "post8"),
            post4Es.copy(id = "post9"),
            post5Es.copy(id = "post10"),
        ),
    )
