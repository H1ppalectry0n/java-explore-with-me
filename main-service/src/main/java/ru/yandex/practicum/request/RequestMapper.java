package ru.yandex.practicum.request;

import jakarta.annotation.Nonnull;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;

public class RequestMapper {

    public static ParticipationRequestDto toDto(@Nonnull RequestModel requestModel) {
        return ParticipationRequestDto.builder()
                .id(requestModel.getId())
                .event(requestModel.getEvent().getId())
                .requester(requestModel.getRequester().getId())
                .status(requestModel.getRequestStatus())
                .created(requestModel.getCreated())
                .build();
    }
}
