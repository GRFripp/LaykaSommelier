package com.example.laykasommelier.data.local.mapper
import com.example.laykasommelier.data.local.entities.*
import com.example.laykasommelier.network.dto.*
fun DrinkDto.toEntity() = Drink(
    drinkID = id,
    drinkName = name,
    drinkType = type,
    drinkSubType = subType,
    drinkCountry = country,
    drinkProducer = producer,
    drinkAged = aged,
    drinkAbv = abv,
    drinkImageUrl = imageUrl
)

fun CocktailDto.toEntity() = Cocktail(
    cocktailID = id,
    cocktailName = name,
    cocktailVolume = volume,
    cocktailAcidity = acidity,
    cocktailSugarLevel = sugarLevel,
    cocktailAbv = abv,
    cocktailGlass = glass,
    cocktailMakingMethodID = makingMethodId,
    cocktailDescription = description,
    cocktailAuthor = author,
    cocktailServing = serving,
    cocktailImageUrl = imageUrl
)

fun CocktailCreateRequest.toEntity(id: Long) = Cocktail(
    cocktailID = id,
    cocktailName = name,
    cocktailVolume = volume,
    cocktailAcidity = acidity,
    cocktailSugarLevel = sugarLevel,
    cocktailAbv = abv,
    cocktailGlass = glass,
    cocktailMakingMethodID = makingMethodId,
    cocktailDescription = description,
    cocktailAuthor = author,
    cocktailServing = serving,
    cocktailImageUrl = imageUrl
)

fun IngredientDto.toEntity() = Ingredient(
    ingredientID = id,
    ingredientName = name,
    ingredientAcidity = acidity,
    ingredientSugarLevel = sugarLevel,
    ingredientAbv = abv,
    ingredientImageUrl = imageUrl
)

fun MakingMethodDto.toEntity() = MakingMethod(
    makingMethodID = id,
    makingMethodName = name,
    makingMethodDilution = dilution
)

fun DescriptorCategoryDto.toEntity() = DescriptorCategory(
    descriptorCategoryID = id,
    descriptorCategoryName = name,
    descriptorCategoryColor = color
)

fun DescriptorDto.toEntity() = Descriptor(
    descriptorID = id,
    descriptorName = name,
    descriptorCategory = categoryId   // обрати внимание: в entity это categoryId (Long)
)

fun CocktailIngredientDto.toEntity() = CocktailIngredient(
    cocktailID = cocktailId,
    ingredientID = ingredientId,
    ingredientVolume = volume
)

fun IngredientDescriptorDto.toEntity() = IngredientDescriptor(
    ingredientID = ingredientId,
    descriptorID = descriptorId
)

fun SourceDto.toEntity() = Source(
    sourceID = id,
    sourceName = name,
    sourceUrl = url
)

fun ReviewDto.toEntity() = Review(
    reviewID = id,
    reviewedDrinkID = reviewedDrinkId,
    reviewSourceID = sourceId,
    reviewUrl = url
)

fun DescriptorReviewDto.toEntity() = DescriptorReview(
    descriptorID = descriptorId,
    reviewID = reviewId
)

fun EmployeeDto.toEntity() = Employee(
    employeeID = id,
    employeeName = name,
    employeeEmail = email,
    employeePassword = password,
    employeePosition = position
)

fun SuggestionDto.toEntity() = Suggestion(
    suggestionID = id,
    suggestedCocktailID = cocktailId,
    suggestionEmployeeID = employeeId,
    suggestionStatus = status
)