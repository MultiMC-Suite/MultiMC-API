package fr.multimc.api.commons.data.sources.rest.models.responses;

import fr.multimc.api.commons.data.sources.rest.models.UserModel;

public record LoginResponseModel(UserModel user, String token) {

}
