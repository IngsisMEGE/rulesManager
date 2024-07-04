package service

import dto.FormatSnippetWithRulesRedisDTO
import dto.SCASnippetWithRulesRedisDTO

interface PrintScriptService {
    fun queueSnippetFormat(formatSnippet: FormatSnippetWithRulesRedisDTO)

    fun queueSnippetSCA(scaSnippet: SCASnippetWithRulesRedisDTO)
}
