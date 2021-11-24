# <img src="https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/2c457e5f-aa0b-432f-b0b7-30a7e780c408/d8ic07r-5edfc792-cbad-4e95-abe5-729ed6863349.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzJjNDU3ZTVmLWFhMGItNDMyZi1iMGI3LTMwYTdlNzgwYzQwOFwvZDhpYzA3ci01ZWRmYzc5Mi1jYmFkLTRlOTUtYWJlNS03MjllZDY4NjMzNDkuanBnIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.POFYD4-yfP4k4vRny7E527xE-83JeI7sKqrTomh7SL0" width="110" height="146"> Librarian

A simple library that helps us to unit-test Jetpack Paging. The motives behind creating it can be found in this [post](https://engineering.theblueground.com/blog/)

## Installation
In the project's build.gradle file you need to add the maven central repository:
```kotlin
repositories {
    mavenCentral()
}
```

In the module's build.gradle file you need to add the following dependency:
```kotlin
testImplementation("com.quadible:librarian:1.0.1")
```

## Usage
First, you need to invoke `toPagination` on an emitted `PagingData` (There is no difference if `PagingData` is emitted from `LiveData`, `Flow` or `Observable`) and then you can use the returned `Pagination` object to make assertions.

```kotlin
// We assume that PagingData will emit three pages
// The first page contains: 1,2,3
// The second page contains: 4,5,6
// The third page contains: 7,8,9

val pagination = viewModel.pagesLiveData.getOrAwaitValue().toPagination()

// Then we can make the following assertions
assertEquals(false, pagination.isEmpty)
assertEquals(3, pagination.count)
assertEquals(listOf(1,2,3), pagination.first())
assertEquals(listOf(7,8,9), pagination.last())
assertEquals(listOf(4,5,6), pagination.pageAt(1))
assertEquals(listOf(1,2,3,4,5,6), pagination.loadedAt(1))
assertEquals(listOf(1,2,3,4,5,6,7,8,9), pagination.fullyLoaded())
```

For `PagingData` that is emitted from a `Flow` you can choose between making the above assertions with turbine or you can you use the turbine like API that we introduced:

```kotlin
viewModel.pagesFlow.testPages {
    val pagination = awaitPages()

    // Make the same assertions as we do in the above example

    ignoreRemaining() // Or you can use awaitNoMore() if you do not expect any other pages
}
```