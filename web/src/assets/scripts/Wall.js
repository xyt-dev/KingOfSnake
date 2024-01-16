import { GameObj } from './GameObj.js';
export class Wall extends GameObj {
    constructor(r, c, gamemap) {
        super();
        this.r = r;
        this.c = c;
        this.gamemap = gamemap;
        this.color = "#B49673";
    }

    start() {
    }

    update() { // 每秒执行一次
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        ctx.fillRect(this.c*L, this.r*L, L, L) // 一块一块渲染
    }
}