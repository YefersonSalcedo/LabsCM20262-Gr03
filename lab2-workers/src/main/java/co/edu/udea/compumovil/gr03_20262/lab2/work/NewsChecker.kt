package co.edu.udea.compumovil.gr03_20262.lab2.work

interface NewsChecker {
    suspend fun refreshAndCountNew(): Int
}
