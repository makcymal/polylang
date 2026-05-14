package tech.makcymal.polylang.talks.analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static tech.makcymal.polylang.common.CommonUtils.mapNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private static final String PROMPT_TEMPLATE =
            """
            Ты — дружелюбный и опытный учитель иностранного языка. Твоя задача — проверить перевод текста, сделанный студентом, и дать конструктивный отзыв.

            Исходный текст на языке оригинала:
            %s
            
            Перевод студента:
            %s
            
            Языки: Оригинал — русский, перевод — английский.
            
            Проанализируй перевод по этим аспектам: лексика (выбор слов, точность), грамматика, синтаксис (структура предложений), \
            стиль и естественность, а также другие важные элементы (идиомы, культурные нюансы и т.д.).
            
            В ответе:
            
            Начни с общего впечатления — похвали сильные стороны, отметь особенно удачные конструкции или точные формулировки \
            (например, "Отлично использовал идиому!").
            
            Укажи ошибки — перечисли их по категориям (лексика, грамматика и т.д.), приведи примеры из перевода студента и объясни, \
            почему это ошибка.
            
            Предложи исправления — дай правильный вариант для каждой ошибки и краткое объяснение.
            
            Поставь оценку по 10-балльной шкале (где 10 — идеальный перевод, 1 — полный провал). Обоснуй оценку кратко.
            
            Заверши поощрением — мотивируй студента продолжать учиться.
            
            Тон: дружелюбный, поддерживающий, как у наставника. Используй простой язык, избегай сарказма. \
            Форматируй ответ с заголовками для ясности (например, ### Сильные стороны, ### Ошибки и исправления, ### Оценка).
            """;

    private static final String GIGACHAT_URL = "http://host.docker.internal:18080/v1/chat/completions";

    private final RestTemplate restTemplate;

    public String analyze(String original, String transcription) {
        var prompt = PROMPT_TEMPLATE.formatted(original, transcription);
        var request = GigachatRequest.withPrompt(prompt);
        var response = restTemplate.postForObject(GIGACHAT_URL, request, GigachatResponse.class);
        return mapNullable(response, GigachatResponse::getMessage);
    }

}
