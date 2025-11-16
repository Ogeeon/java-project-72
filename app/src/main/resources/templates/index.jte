@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.BasePage
@import hexlet.code.util.FlashType
@param BasePage page = null
@param String flash = null
@param FlashType flashType = null

@template.layout.page(
    content = @`
        <div class="container mt-5">
            <div class="row justify-content-center">
                <div class="col-md-8 col-lg-6">
                    <div class="card">
                        <div class="card-body">
                            <h3 class="card-title text-center mb-4">Введите URL сайта</h3>
                            <form action="${NamedRoutes.urlsPath()}" method="POST">
                                <div class="input-group">
                                    <input type="text" class="form-control" id="urlInput" name="url" placeholder="Ссылка" required>
                                    <button type="submit" class="btn btn-primary">Добавить</button>
                                </div>
                                <div class="form-text">
                                    Например: https://example.com
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `,
    page = page,
    activeNav = NamedRoutes.rootPath(),
    flash = flash,
    flashType = flashType
)
