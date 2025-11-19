@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.util.FlashType
@import java.time.format.DateTimeFormatter

@param UrlsPage page
@param String flash = null
@param FlashType flashType = null

@template.layout.page(
    content = @`
        <div class="container mt-4">
            <h2>Сайты</h2>
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Имя</th>
                        <th>Последняя проверка</th>
                        <th>Код ответа</th>
                    </tr>
                </thead>
                <tbody>
                @for(var url: page.getUrls())
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a></td>
                    !{ var lastCheck = page.getChecks().get(url.getId()); }
                    <td>${lastCheck == null ? "" : lastCheck.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}</td>
                    <td>${lastCheck == null ? "" : String.valueOf(lastCheck.getStatusCode())}</td>

                </tr>
                @endfor
                </tbody>
            </table>
        </div>
    `,
    page = page,
    activeNav = NamedRoutes.urlsPath(),
    flash = flash,
    flashType = flashType
)