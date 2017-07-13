local activity = ...

local Article = luajava.bindClass('cn.nekocode.hot.data.model.Article')
local SpannableString = luajava.bindClass('android.text.SpannableString')
local ArrayList = luajava.bindClass('java.util.ArrayList')

function getArticles(page)
    local articleList = ArrayList.new()
    local request = okhttp.get("http://i.jandan.net")

    if (page < 3) then
        for i=1, 10 do
            local article = Article.new()
            article:setCoverUrl("http://file25.mafengwo.net/M00/A8/DC/wKgB4lImBjiAXbFhAA1RSMu2P6s60.jpeg");
            article:setTitle(SpannableString.new("Article " .. page .. "-" .. i));
            article:setDescription(SpannableString.new(string.sub(request.body, 1, 20)));
            articleList:add(article)
        end
    end

    return articleList
end