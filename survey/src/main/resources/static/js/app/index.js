var main = {
    init : function () {
        var _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });

        // 수정 버튼 이벤트 등록
        $('#btn-update').on('click', function () {
            _this.update();
        });

        // 삭제 버튼 이벤트 등록
        $('#btn-delete').on('click', function () {
            _this.delete();
        });

        $('#btn-like').on('click', function () {
            _this.like();
        });

        $('#btn-comment-save').on('click', function () {
            _this.commentSave();
        });

        // 동적으로 생성된 버튼이므로 document에 이벤트 위임
        $(document).on('click', '.btn-reply-toggle', function() {
            var id = $(this).data('id');
            $('#reply-form-' + id).toggleClass('d-none');
        });

        $(document).on('click', '.btn-reply-save', function() {
            var id = $(this).data('id');
            var content = $('#reply-content-' + id).val();
            _this.replySave(id, content);
        });
    },
    save : function () {
        var data = {
            title: $('#title').val(),
            content: $('#content').val()
        };

        if (data.title.trim() === "" || data.content.trim() === "") {
            alert("제목과 내용을 모두 입력해주세요.");
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 등록되었습니다.');
            window.location.reload();
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },

    update : function () {
        var data = {
            title: $('#title').val(),
            content: $('#content').val()
        };

        var id = $('#id').val();

        $.ajax({
            type: 'PUT',
            url: '/api/v1/posts/'+ id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 수정되었습니다.');
            window.location.href = '/board';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },

    delete : function () {
        var id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/posts/'+ id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8'
        }).done(function() {
            alert('글이 삭제되었습니다.');
            window.location.href = '/board';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },

    like : function () {
        var id = $('#postsId').val();
        $.ajax({
            type: 'POST',
            url: '/api/v1/posts/'+id+'/likes',
        }).done(function(isLiked) {
            window.location.reload();
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },

    commentSave : function () {
        var data = {
            postsId: $('#postsId').val(),
            content: $('#comment-content').val(),
            parentId: null
        };

        if (!data.content || data.content.trim() === "") {
            alert("댓글 내용을 입력해주세요.");
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts/'+ data.postsId +'/comments',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('댓글이 등록되었습니다.');
            window.location.reload();
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },

    replySave : function (parentId, content) {
        var data = {
            postsId: $('#postsId').val(),
            content: content,
            parentId: parentId
        };

        if (!data.content || data.content.trim() === "") {
            alert("답글 내용을 입력해주세요.");
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts/'+ data.postsId +'/comments',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('답글이 등록되었습니다.');
            window.location.reload();
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};

main.init();