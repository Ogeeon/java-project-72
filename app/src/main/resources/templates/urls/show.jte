@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.util.FlashType
@import java.time.format.DateTimeFormatter

@param UrlPage page
@param String flash = null
@param FlashType flashType = null

@template.layout.page(
    content = @`
        <div class="container mt-4">
            <h2>Сайт: ${page.getUrl().getPageUrl()}</h2>
            <table class="table">
                <tbody>
                    <tr><td>ID</td><td>${page.getUrl().getId()}</td></tr>
                    <tr><td>Имя</td><td>${page.getUrl().getPageUrl()}</td></tr>
                    <tr><td>Дата создания</td><td>${page.getUrl().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}</td></tr>
                </tbody>
            </table>
        </div>
    `,
    page = page,
    activeNav = null,
    flash = flash,
    flashType = flashType
)
