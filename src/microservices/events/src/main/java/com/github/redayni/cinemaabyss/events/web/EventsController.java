package com.github.redayni.cinemaabyss.events.web;

import com.github.redayni.cinemaabyss.events.service.EventPublisher;
import com.github.redayni.cinemaabyss.events.web.dto.EventDto;
import com.github.redayni.cinemaabyss.events.web.dto.EventResponse;
import com.github.redayni.cinemaabyss.events.web.dto.MovieEventRequest;
import com.github.redayni.cinemaabyss.events.web.dto.PaymentEventRequest;
import com.github.redayni.cinemaabyss.events.web.dto.UserEventRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    private final EventPublisher publisher;
    private final ObjectMapper objectMapper;

    public EventsController(
        EventPublisher publisher,
        ObjectMapper objectMapper
    ) {
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/movie")
    public ResponseEntity<EventResponse> movie(@Valid @RequestBody MovieEventRequest request) throws Exception {
        String eventId = "movie-" + request.movieId() + "-" + request.action();
        SendResult<String, String> result = publisher.publishMovieEvent(eventId, request);

        return buildResponse(eventId, "movie", request, result);
    }

    @PostMapping("/user")
    public ResponseEntity<EventResponse> user(@Valid @RequestBody UserEventRequest request) throws Exception {
        String eventId = "user-" + request.userId() + "-" + request.action();
        SendResult<String, String> result = publisher.publishUserEvent(eventId, request);

        return buildResponse(eventId, "user", request, result);
    }

    @PostMapping("/payment")
    public ResponseEntity<EventResponse> payment(@Valid @RequestBody PaymentEventRequest request) throws Exception {
        String eventId = "payment-" + request.paymentId() + "-user-" + request.userId() + "-amount-" + request.amount();
        SendResult<String, String> result = publisher.publishPaymentEvent(eventId, request);

        return buildResponse(eventId, "payment", request, result);
    }

    private ResponseEntity<EventResponse> buildResponse(
        String eventId,
        String type,
        Object request,
        SendResult<String, String> result
    ) {
        var meta = result.getRecordMetadata();
        var payloadNode = objectMapper.valueToTree(request);
        var event = new EventDto(eventId, type, Instant.now(), payloadNode);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new EventResponse("success", meta.partition(), meta.offset(), event));
    }
}
