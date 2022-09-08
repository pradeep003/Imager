package com.ftcoding.imager.use_cases.user

data class UserUseCases (
    val getMyProfileUseCases: GetMyProfileUseCases,
    val getUserByUsernameUseCases: GetUserByUsernameUseCases,
    val updateMyProfileUseCases: UpdateMyProfileUseCases
        )