package jp.jiho.cmpprofiledemo.ui.common


import cmpprofiledemo.composeapp.generated.resources.Res
import cmpprofiledemo.composeapp.generated.resources.error_http_bad_request
import cmpprofiledemo.composeapp.generated.resources.error_http_not_found
import cmpprofiledemo.composeapp.generated.resources.error_http_server_error
import cmpprofiledemo.composeapp.generated.resources.error_http_unauthorized
import cmpprofiledemo.composeapp.generated.resources.error_http_unknown
import cmpprofiledemo.composeapp.generated.resources.error_offline
import cmpprofiledemo.composeapp.generated.resources.error_unknown
import io.kotest.matchers.shouldBe
import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.ErrorKind
import kotlin.test.Test

class AppErrorExtTest {

    @Test
    fun `ClientOffline maps to offline string resource`() {
        AppError.ClientOffline.toUserMessage() shouldBe Res.string.error_offline
    }

    @Test
    fun `Http BadRequest maps to bad request string resource`() {
        AppError.Http(400, ErrorKind.BadRequest, "400 Bad Request").toUserMessage() shouldBe Res.string.error_http_bad_request
    }

    @Test
    fun `Http Unauthorized maps to unauthorized string resource`() {
        AppError.Http(401, ErrorKind.Unauthorized, "401 Unauthorized").toUserMessage() shouldBe Res.string.error_http_unauthorized
    }

    @Test
    fun `Http NotFound maps to not found string resource`() {
        AppError.Http(404, ErrorKind.NotFound, "404 Not Found").toUserMessage() shouldBe Res.string.error_http_not_found
    }

    @Test
    fun `Http ServerError maps to server error string resource`() {
        AppError.Http(500, ErrorKind.ServerError, "500 Internal Server Error").toUserMessage() shouldBe Res.string.error_http_server_error
    }

    @Test
    fun `Http Unknown maps to http unknown string resource`() {
        AppError.Http(0, ErrorKind.Unknown, "0 Unknown").toUserMessage() shouldBe Res.string.error_http_unknown
    }

    @Test
    fun `Unknown maps to unknown string resource`() {
        AppError.Unknown("detail").toUserMessage() shouldBe Res.string.error_unknown
    }
}
