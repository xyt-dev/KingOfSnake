
export default {
    state: {
        status: "matching", // matching: 匹配界面, playing: 对战界面
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
        rows: 0,
        cols: 0,
        a_id: 0,
        a_sx: 0,
        a_sy: 0,
        b_id: 0,
        b_sx: 0,
        b_sy: 0,
        role: "",
        gameObject: null,
        result: "none", // none D A B
    },
    getters: {

    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGame(state, data) {
            state.gamemap = data.gamemap;
            state.rows = data.rows;
            state.cols = data.cols;
            state.a_id = data.a_id;
            state.a_sx = data.a_sx;
            state.a_sy = data.a_sy;
            state.b_id = data.b_id;
            state.b_sx = data.b_sx;
            state.b_sy = data.b_sy;
            state.role = data.role;
            // console.log("updateGameMap:", data)
        },
        updateGameObject(state, gameObject) {
            state.gameObject = gameObject;
        },
        updateResult(state, result) {
            state.result = result;
        }
    },
    actions: {

    },
    modules: {

    }
}