@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.BasePage
@import hexlet.code.util.FlashType
@import hexlet.code.util.Flash
@import gg.jte.Content
@param Content content
@param BasePage page = null
@param String activeNav = null
@param String flash = null
@param FlashType flashType = null

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${NamedRoutes.rootPath()}">Анализатор страниц</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link ${activeNav != null && activeNav.equals(NamedRoutes.rootPath()) ? "active" : ""}" href="${NamedRoutes.rootPath()}">Главная</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeNav != null && activeNav.equals(NamedRoutes.urlsPath()) ? "active" : ""}" href="${NamedRoutes.urlsPath()}">Сайты</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class="container mt-3">
    @if(flash != null)
        <div class="alert ${Flash.bootstrapClass(flashType)} alert-dismissible fade show" role="alert">
            <p>${flash}</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    @endif
</div>
${content}
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>