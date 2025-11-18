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
            <h2>Сайт: ${page.getUrl().getName()}</h2>
            <table class="table">
                <tbody>
                    <tr><td>ID</td><td>${page.getUrl().getId()}</td></tr>
                    <tr><td>Имя</td><td>${page.getUrl().getName()}</td></tr>
                    <tr><td>Дата создания</td><td>${page.getUrl().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}</td></tr>
                </tbody>
            </table>
            <h2 class="mt-5">Проверки</h2>
            <form method="post" action="${NamedRoutes.checkPath(page.getUrl().getId())}">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>

            <table class="table table-bordered table-hover mt-3">
                <tr>
                    <th class="col-1">ID</th><th class="col-1">Код ответа</th><th>title</th><th>h1</th><th>description</th><th class="col-2">Дата проверки</th>
                </tr>
                <tbody>
                @if (page.getChecks() != null)
                    @for(var check: page.getChecks())
                        <tr>
                            <td>${check.getId()}</td>
                            <td>${check.getStatusCode()}</td>
                            <td>${check.getTitle()}</td>
                            <td>${check.getH1()}</td>
                            <td>${check.getDescription()}</td>
                            <td>${check.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}</td>
                        </tr>
                    @endfor
                @endif
                </tbody>
            </table>
        </div>
    `,
    page = page,
    activeNav = null,
    flash = flash,
    flashType = flashType
)
