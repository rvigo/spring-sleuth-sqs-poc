package com.rvigo.sleuthSqs.interceptors

import brave.Tracer
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.MDC
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.stereotype.Component

private const val TRACE_ID: String = "traceId"
private const val SPAN_ID: String = "spanId"

@Aspect
@Component
class SQSListenerInterceptor(private val tracer: Tracer) {

    @Around("@annotation(org.springframework.cloud.aws.messaging.listener.annotation.SqsListener)")
    fun handle(joinPoint: ProceedingJoinPoint) {
        getHeaders(joinPoint)?.let { headers ->
            startContext(headers[TRACE_ID]?.toString())
        }
        try {
            joinPoint.proceed()
        } finally {
            finishContext()
        }
    }

    private fun getHeaders(joinPoint: JoinPoint) =
        getIndexedHeaders(joinPoint)?.let { headers -> joinPoint.args[headers.index] as Map<*, *> }

    private fun getIndexedHeaders(joinPoint: JoinPoint) =
        (joinPoint.signature as MethodSignature).method.parameters.withIndex().filterIndexed { _, index ->
            index.value.getAnnotation(
                Headers::class.java
            ) != null
        }.firstOrNull()

    private fun startContext(traceId: String?) {
        finishContext()
        setTraceId(traceId)
        setSpanId()
    }

    private fun setTraceId(traceId: String?) {
        MDC.put(TRACE_ID, traceId ?: newTraceId())
    }

    private fun setSpanId() {
        MDC.put(SPAN_ID, newSpanId())
    }

    private fun newSpanId() = tracer.nextSpan().context().spanIdString()
    private fun newTraceId() = tracer.newTrace().context().traceIdString()

    private fun finishContext() = MDC.clear()

}