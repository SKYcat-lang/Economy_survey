const graphInit = {
    init: function () {
        const _this = this;
        _this.loadGraphData();
    },

    loadGraphData: function () {
        const apiUrl = '/api/graph-data';

        fetch(apiUrl)
            .then(response => response.json())
            .then(data => {
                this.drawChart(data);
            })
            .catch(error => console.error('그래프 데이터 로딩 실패:', error));
    },

    // [핵심 변경] Python의 cmap='RdYlBu_r'와 유사한 그라데이션 로직 구현
    // 가계부채(debt)가 낮으면(20~60) 파랑, 높으면(90~110) 빨강
    getGradientColor: function (debt) {
        if (debt === null || debt === undefined) return 'rgba(200, 200, 200, 0.5)';

        // 1. 값 정규화 (Min 20 ~ Max 110) -> 0.0 ~ 1.0 범위로 변환
        let ratio = (debt - 20) / (110 - 20);
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;

        // 2. 색상 보간 (Blue -> Yellow -> Red)
        // Blue (54, 162, 235), Yellow (255, 206, 86), Red (255, 99, 132)
        let r, g, b;

        if (ratio < 0.5) {
            // Blue -> Yellow 구간 (0.0 ~ 0.5)
            // ratio를 0~1로 재조정
            const subRatio = ratio * 2;
            r = 54 + (255 - 54) * subRatio;
            g = 162 + (206 - 162) * subRatio;
            b = 235 + (86 - 235) * subRatio;
        } else {
            // Yellow -> Red 구간 (0.5 ~ 1.0)
            const subRatio = (ratio - 0.5) * 2;
            r = 255 + (255 - 255) * subRatio; // Red stays high
            g = 206 + (99 - 206) * subRatio;  // Green drops
            b = 86 + (132 - 86) * subRatio;   // Blue adjusts
        }

        return `rgba(${Math.floor(r)}, ${Math.floor(g)}, ${Math.floor(b)}, 0.8)`;
    },

    drawChart: function (jsonData) {
            const ctx = document.getElementById('phaseSpaceChart').getContext('2d');

            // 1. 표시할 특정 연도 설정 (시작년도 2005 포함)
            const targetYears = [2005, 2007, 2015, 2021, 2024];

            const countryStyles = {
                'KR': { color: '#6c5ce7', label: '한국' },
                'US': { color: '#0984e3', label: '미국' },
                'CN': { color: '#d63031', label: '중국' },
                'JP': { color: '#b2bec3', label: '일본' },
                'DE': { color: '#00b894', label: '독일' },
                'VN': { color: '#e84393', label: '베트남'},
                'RU': { color: '#2d3436', label: '러시아'},
                'IN': { color: '#e67e22', label: '인도' }
            };

            const datasets = [];

            for (const [country, values] of Object.entries(jsonData)) {
                const style = countryStyles[country] || { color: '#999', label: country };

                // 연도순 정렬 (선이 꼬이지 않게)
                values.sort((a, b) => a.year - b.year);

                const dataPoints = values.map(v => ({
                    x: v.TotalCredit_GDP,
                    y: v.Capex_GDP,
                    // 버블 크기 계산 (시총 기반)
                    r: v.MarketCap_GDP ? Math.sqrt(v.MarketCap_GDP) : 3,
                    rawYear: v.year,
                    debt: v.HH_Debt
                }));

                datasets.push({
                    label: style.label,
                    data: dataPoints,
                    type: 'scatter', // 선과 점을 모두 제어하기 위해 scatter 사용
                    showLine: true,  // 궤적(선)은 항상 표시

                    // [핵심 수정] 특정 연도에만 점(공점) 표시
                    pointRadius: (context) => {
                        const val = context.raw;
                        // 데이터가 없거나, 타겟 연도가 아니면 반지름 0 (숨김)
                        if (!val || !targetYears.includes(val.rawYear)) {
                            return 0;
                        }
                        // 타겟 연도면 원래 크기 반환
                        return val.r;
                    },
                    // 마우스 올렸을 때도 타겟 연도가 아니면 커지지 않게 설정
                    pointHoverRadius: (context) => {
                        const val = context.raw;
                        if (!val || !targetYears.includes(val.rawYear)) {
                            return 0;
                        }
                        return val.r + 2; // 강조 효과
                    },

                    // 점 스타일
                    backgroundColor: style.color,
                    borderColor: '#fff',
                    borderWidth: 1,

                    // 선 스타일 (그라데이션 적용)
                    borderWidth: 3,
                    tension: 0.4,
                    segment: {
                        borderColor: (ctx) => {
                            if(ctx.p1.raw) {
                                return this.getGradientColor(ctx.p1.raw.debt);
                            }
                            return 'rgba(0,0,0,0.1)';
                        },
                        borderWidth: 3
                    },
                    borderColor: 'rgba(0,0,0,0.1)'
                });
            }

            if (window.myChart) {
                window.myChart.destroy();
            }

            window.myChart = new Chart(ctx, {
                type: 'scatter',
                data: { datasets: datasets },
                options: {
                    responsive: true,
                    interaction: {
                        mode: 'nearest',
                        intersect: false // 점이 없어도 선 근처에 마우스 대면 툴팁 표시
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: '금융의 사회적 지배력 (민간신용/GDP %)',
                                font: { weight: 'bold', size: 14 }
                            },
                            grid: { color: '#f0f0f0' }
                        },
                        y: {
                            title: {
                                display: true,
                                text: '자본의 생산적 실현 (설비투자/GDP %)',
                                font: { weight: 'bold', size: 14 }
                            },
                            grid: { color: '#f0f0f0' }
                        }
                    },
                    plugins: {
                        legend: {
                            position: 'bottom',
                            labels: { usePointStyle: true, padding: 20 }
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const p = context.raw;
                                    return `${context.dataset.label} ${p.rawYear}년: ` +
                                           `가계부채 ${p.debt}%`;
                                }
                            }
                        },
                        countryLabels: {}
                    }
                },
                plugins: [{
                    id: 'countryLabels',
                    afterDatasetsDraw(chart) {
                        const { ctx } = chart;
                        chart.data.datasets.forEach((dataset, i) => {
                            const meta = chart.getDatasetMeta(i);
                            if (!meta.hidden) {
                                const lastIndex = dataset.data.length - 1;
                                const lastPoint = meta.data[lastIndex];
                                if (lastPoint) {
                                    ctx.save();
                                    ctx.font = 'bold 12px Malgun Gothic';
                                    ctx.fillStyle = dataset.backgroundColor;
                                    ctx.textAlign = 'left';
                                    ctx.textBaseline = 'middle';
                                    ctx.fillText(dataset.label, lastPoint.x + 10, lastPoint.y);
                                    ctx.restore();
                                }
                            }
                        });
                    }
                }]
            });
        }
};

document.addEventListener('DOMContentLoaded', function () {
    graphInit.init();
});