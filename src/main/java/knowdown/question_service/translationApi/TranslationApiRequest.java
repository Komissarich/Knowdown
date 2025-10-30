package knowdown.question_service.translationApi;

public record TranslationApiRequest (
        String q,
        String source,
        String target
) {}
