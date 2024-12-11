package space.geek.config.server.jolokia

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import mu.KotlinLogging
import org.jolokia.http.HttpRequestHandler
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.server.ServerWebExchange
import java.io.IOException

@WebEndpoint(id = "jolokia")
class JolokiaEndpoint(
    private val requestHandler: HttpRequestHandler,
) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/**")
    fun get(exchange: ServerWebExchange): String {
        val req = exchange.request
        val pathInfo = pathInfo(req)

        return requestHandler.handleGetRequest(req.uri.toString(), pathInfo, queryParams(req)).toJSONString()
    }

    @PostMapping
    fun post(exchange: ServerWebExchange): Flow<String> {
       return exchange.request.body
           .asFlow()
           .map {
               val input = it.asInputStream()
               val req = exchange.request
               try {
                   requestHandler.handlePostRequest(
                       req.uri.toString(),
                       input,
                       null,
                       queryParams(req),
                   ).toJSONString()
               } catch (e: IOException) {
                   log.error("Error POST Jolokia", e)
                   throw e
               }
           }
    }

    private fun pathInfo(req: ServerHttpRequest): String {
        return req.path.subPath(4).toString()
    }

    private fun queryParams(req: ServerHttpRequest): MutableMap<String, Array<String>> {
        return req.queryParams.mapValues { e -> e.value.toTypedArray() }.toMutableMap()
    }

}