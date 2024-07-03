package service.implementation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dto.FormatSnippetWithRulesRedisDTO
import dto.SCASnippetWithRulesRedisDTO
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import service.PrintScriptService

@Service
class PrintScriptServiceImpl(private val redisTemplate: RedisTemplate<String, Any>) : PrintScriptService {
    private val objectMapper = jacksonObjectMapper()

    override fun queueSnippetFormat(formatSnippet: FormatSnippetWithRulesRedisDTO) {
        val requestData = objectMapper.writeValueAsString(formatSnippet)
        redisTemplate.opsForList().rightPush("snippet_formatting_queue", requestData)
    }

    override fun queueSnippetSCA(scaSnippet: SCASnippetWithRulesRedisDTO) {
        val requestData = objectMapper.writeValueAsString(scaSnippet)
        redisTemplate.opsForList().rightPush("snippet_SCA_queue", requestData)
    }
}
